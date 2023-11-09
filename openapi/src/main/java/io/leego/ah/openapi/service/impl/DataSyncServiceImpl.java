package io.leego.ah.openapi.service.impl;

import io.leego.ah.openapi.config.DataSyncProperties;
import io.leego.ah.openapi.dto.DataSyncDTO;
import io.leego.ah.openapi.entity.QAuction;
import io.leego.ah.openapi.entity.QAuctionLog;
import io.leego.ah.openapi.entity.QExchange;
import io.leego.ah.openapi.repository.AuctionLogRepository;
import io.leego.ah.openapi.repository.AuctionRepository;
import io.leego.ah.openapi.repository.ExchangeRepository;
import io.leego.ah.openapi.repository.QuerydslRepository;
import io.leego.ah.openapi.service.DataSyncService;
import io.leego.ah.openapi.util.QPredicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * @author Leego Yih
 */
@Component
public class DataSyncServiceImpl implements DataSyncService {
    public static final int TYPE_AUCTION = 0;
    public static final int TYPE_AUCTION_LOG = 1;
    public static final int TYPE_EXCHANGE = 2;
    private static final Logger logger = LoggerFactory.getLogger(DataSyncServiceImpl.class);
    private final AuctionRepository auctionRepository;
    private final AuctionLogRepository auctionLogRepository;
    private final ExchangeRepository exchangeRepository;
    private final EntityManager entityManager;
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;
    private final Map<String, Sync> syncs;

    public DataSyncServiceImpl(AuctionRepository auctionRepository, AuctionLogRepository auctionLogRepository, ExchangeRepository exchangeRepository,
                               EntityManager entityManager, EntityManagerFactoryBuilder entityManagerFactoryBuilder, DataSyncProperties dataSyncProperties) {
        this.auctionRepository = auctionRepository;
        this.auctionLogRepository = auctionLogRepository;
        this.exchangeRepository = exchangeRepository;
        this.entityManager = entityManager;
        this.entityManagerFactoryBuilder = entityManagerFactoryBuilder;
        this.syncs = new ConcurrentHashMap<>();
        for (Map.Entry<String, DataSyncProperties.DataSource> entry : dataSyncProperties.getDatasource().entrySet()) {
            String name = entry.getKey();
            DataSyncProperties.DataSource config = entry.getValue();
            syncs.put(name, newSync(name, config));
        }
    }

    @Override
    public void sync(DataSyncDTO dto) {
        if (CollectionUtils.isEmpty(dto.getType())) {
            dto.setType(List.of(TYPE_AUCTION, TYPE_AUCTION_LOG, TYPE_EXCHANGE));
        }
        for (var type : dto.getType()) {
            if (type == TYPE_AUCTION) {
                var predicate = QPredicate.create().and(QAuction.auction.updatedTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, auctionRepository, "sync auction");
            } else if (type == TYPE_AUCTION_LOG) {
                var predicate = QPredicate.create().and(QAuctionLog.auctionLog.createdTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, auctionLogRepository, "sync auction log");
            } else if (type == TYPE_EXCHANGE) {
                var predicate = QPredicate.create().and(QExchange.exchange.createdTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, exchangeRepository, "sync exchange");
            }
        }
    }

    private void syncData(QPredicate predicate, QuerydslRepository<?> repo, String tag) {
        if (predicate == null || predicate.isEmpty()) {
            return;
        }
        var entities = (List<?>) repo.findAll(predicate, Sort.by(Sort.Direction.DESC, "id"));
        if (!entities.isEmpty()) {
            var size = 50;
            var parts = partition(entities, size);
            for (var i = 0; i < parts.size(); i++) {
                var sub = parts.get(i);
                update(sub, "%s %d/%d".formatted(tag, (i * size + sub.size()), entities.size()));
            }
        }
    }

    @Override
    public void create(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::merge);
    }

    @Override
    public void update(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::merge);
    }

    @Override
    public void delete(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::remove);
    }

    private void execute(Collection<?> entities, String tag, BiConsumer<EntityManager, Object> exec) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        for (var sync : syncs.values()) {
            var name = sync.getName();
            var size = entities.size();
            logger.info("Syncing data({}) to {} ({})", size, name, tag);
            sync.getExecutorService().execute(() -> {
                boolean ok = false;
                while (!ok) {
                    var begin = System.currentTimeMillis();
                    var em = sync.getEntityManager();
                    var tx = em.getTransaction();
                    try {
                        tx.begin();
                        for (Object entity : entities) {
                            exec.accept(em, entity);
                        }
                        em.flush();
                        tx.commit();
                        var end = System.currentTimeMillis();
                        logger.info("Synced data({}) to {} in {} ms ({})", size, name, end - begin, tag);
                        ok = true;
                    } catch (Exception e) {
                        logger.error("Failed to sync data({}) to {} ({})", size, name, tag, e);
                        if (tx.isActive()) {
                            tx.rollback();
                        }
                        em.close();
                        try {
                            sync.reset();
                        } catch (Exception e2) {
                            logger.error("Failed to reset datasource({})", name);
                        }
                    }
                }
            });
        }
    }

    private Sync newSync(String name, DataSyncProperties.DataSource config) {
        return new Sync(name, config, Executors.newSingleThreadExecutor(), newEntityManagerFactory(name, config));
    }

    private EntityManagerFactory newEntityManagerFactory(String name, DataSyncProperties.DataSource config) {
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(config.getDriverClassName())
                .url(config.getUrl())
                .username(config.getUsername())
                .password(config.getPassword())
                .build();
        Map<String, Object> properties = new HashMap<>(entityManager.getEntityManagerFactory().getProperties());
        properties.put("jakarta.persistence.nonJtaDataSource", dataSource);
        properties.put("javax.persistence.nonJtaDataSource", dataSource);
        properties.put("hibernate.connection.datasource", dataSource);
        LocalContainerEntityManagerFactoryBean factoryBean = entityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("io.leego.ah.openapi.entity")
                .persistenceUnit(name)
                .properties(properties)
                .build();
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    private <E> List<List<E>> partition(final List<E> source, final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        int total = source.size();
        if (total <= size) {
            return List.of(source);
        }
        return IntStream.range(0, total % size == 0 ? total / size : total / size + 1)
                .mapToObj(i -> source.subList(i * size, Math.min((i + 1) * size, total)))
                .toList();
    }

    @Data
    final class Sync {
        private final String name;
        private final DataSyncProperties.DataSource config;
        private final ExecutorService executorService;
        private EntityManagerFactory entityManagerFactory;
        private EntityManager entityManagerBean;

        public Sync(String name, DataSyncProperties.DataSource config, ExecutorService executorService, EntityManagerFactory entityManagerFactory) {
            this.name = name;
            this.config = config;
            this.executorService = executorService;
            this.entityManagerFactory = entityManagerFactory;
        }

        public void reset() {
            this.entityManagerFactory = newEntityManagerFactory(this.name, this.config);
            this.entityManagerBean = null;
        }

        public EntityManager getEntityManager() {
            if (entityManagerBean != null && entityManagerBean.isOpen()) {
                return entityManagerBean;
            }
            synchronized (this) {
                if (entityManagerBean != null && entityManagerBean.isOpen()) {
                    return entityManagerBean;
                }
                return entityManagerBean = entityManagerFactory.createEntityManager();
            }
        }
    }
}
