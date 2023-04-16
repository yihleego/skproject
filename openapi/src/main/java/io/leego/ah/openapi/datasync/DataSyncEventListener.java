package io.leego.ah.openapi.datasync;

import io.leego.ah.openapi.config.DataSyncProperties;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * @author Leego Yih
 */
@Component
public class DataSyncEventListener implements ApplicationListener<DataSyncEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DataSyncEventListener.class);
    private final List<Sync> syncs;

    public DataSyncEventListener(EntityManager entityManager, EntityManagerFactoryBuilder builder, DataSyncProperties dataSyncProperties) {
        this.syncs = dataSyncProperties.getDatasource().entrySet().stream()
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
    public void onApplicationEvent(DataSyncEvent event) {
        if (syncs.isEmpty()) {
            return;
        }
        var type = event.getType();
        var tag = event.getTag();
        var source = event.getSource();
        if (source == null) {
            logger.debug("No data to sync ({})", event.getTag());
            return;
        }
        switch (type) {
            case CREATE -> create(source, tag);
            case UPDATE -> update(source, tag);
            case DELETE -> delete(source, tag);
            default -> logger.error("Unsupported type {}", type);
        }
    }

    private void create(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::merge);
    }

    private void update(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::merge);
    }

    private void delete(Collection<?> entities, String tag) {
        execute(entities, tag, EntityManager::remove);
    }

    private void execute(Collection<?> entities, String tag, BiConsumer<EntityManager, Object> exec) {
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
