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
    private final List<Sync> syncs;

    public DataSyncServiceImpl(AuctionRepository auctionRepository, AuctionLogRepository auctionLogRepository, ExchangeRepository exchangeRepository,
                               EntityManager entityManager, EntityManagerFactoryBuilder builder, DataSyncProperties dataSyncProperties) {
        this.auctionRepository = auctionRepository;
        this.auctionLogRepository = auctionLogRepository;
        this.exchangeRepository = exchangeRepository;
        this.syncs = dataSyncProperties.getDatasource().entrySet()
                .stream()
                .map(o -> {
                    DataSource dataSource = DataSourceBuilder.create()
                            .driverClassName(o.getValue().getDriverClassName())
                            .url(o.getValue().getUrl())
                            .username(o.getValue().getUsername())
                            .password(o.getValue().getPassword())
                            .build();
                    Map<String, Object> properties = new HashMap<>(entityManager.getEntityManagerFactory().getProperties());
                    properties.put("jakarta.persistence.nonJtaDataSource", dataSource);
                    properties.put("javax.persistence.nonJtaDataSource", dataSource);
                    properties.put("hibernate.connection.datasource", dataSource);
                    LocalContainerEntityManagerFactoryBean factoryBean = builder
                            .dataSource(dataSource)
                            .packages("io.leego.ah.openapi.entity")
                            .persistenceUnit(o.getKey())
                            .properties(properties)
                            .build();
                    factoryBean.afterPropertiesSet();
                    return new Sync(o.getKey(), Executors.newSingleThreadExecutor(), factoryBean.getObject());
                })
                .toList();
    }

    @Override
    public void sync(DataSyncDTO dto) {
        if (CollectionUtils.isEmpty(dto.getType())) {
            dto.setType(List.of(TYPE_AUCTION, TYPE_AUCTION_LOG, TYPE_EXCHANGE));
        }
        for (Integer type : dto.getType()) {
            if (type == TYPE_AUCTION) {
                QPredicate predicate = QPredicate.create().and(QAuction.auction.updatedTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, auctionRepository, "sync auction");
            } else if (type == TYPE_AUCTION_LOG) {
                QPredicate predicate = QPredicate.create().and(QAuctionLog.auctionLog.createdTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, auctionLogRepository, "sync auction log");
            } else if (type == TYPE_EXCHANGE) {
                QPredicate predicate = QPredicate.create().and(QExchange.exchange.createdTime::between, dto.getBeginTime(), dto.getEndTime());
                syncData(predicate, exchangeRepository, "sync exchange");
            }
        }
    }

    private void syncData(QPredicate predicate, QuerydslRepository<?> repo, String tag) {
        if (predicate == null || predicate.isEmpty()) {
            return;
        }
        List<?> entities = (List<?>) repo.findAll(predicate, Sort.by(Sort.Direction.DESC, "id"));
        if (!entities.isEmpty()) {
            partition(entities, 50).forEach(list -> update(list, tag));
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
        for (var sync : syncs) {
            sync.getExecutorService().execute(() -> {
                var begin = System.currentTimeMillis();
                var name = sync.getName();
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
                    logger.info("Synced data({}) to {} in {} ms ({})", entities.size(), name, end - begin, tag);
                } catch (Exception e) {
                    if (tx.isActive()) {
                        tx.rollback();
                    }
                    logger.error("Failed to sync data({}) to {} ({})", entities.size(), name, tag, e);
                }
            });
        }
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

    static final class Sync {
        private final String name;
        private final ExecutorService executorService;
        private final EntityManagerFactory entityManagerFactory;
        private EntityManager entityManagerBean;

        public Sync(String name, ExecutorService executorService, EntityManagerFactory entityManagerFactory) {
            this.name = name;
            this.executorService = executorService;
            this.entityManagerFactory = entityManagerFactory;
        }

        public String getName() {
            return name;
        }

        public ExecutorService getExecutorService() {
            return executorService;
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
