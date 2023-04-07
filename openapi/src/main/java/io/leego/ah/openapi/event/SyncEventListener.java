package io.leego.ah.openapi.event;

import io.leego.ah.openapi.config.DataSyncProperties;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Leego Yih
 */
@Component
public class SyncEventListener implements ApplicationListener<SyncEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SyncEventListener.class);
    private final List<Sync> syncs;

    public SyncEventListener(EntityManager entityManager, EntityManagerFactoryBuilder builder, DataSyncProperties properties) {
        this.syncs = properties.getDatasource().entrySet().stream()
                .map(o -> {
                    DataSource dataSource = DataSourceBuilder.create()
                            .driverClassName(o.getValue().getDriverClassName())
                            .url(o.getValue().getUrl())
                            .username(o.getValue().getUsername())
                            .password(o.getValue().getPassword())
                            .build();
                    Map<String, Object> emfp = new HashMap<>(entityManager.getEntityManagerFactory().getProperties());
                    emfp.put("jakarta.persistence.nonJtaDataSource", dataSource);
                    emfp.put("javax.persistence.nonJtaDataSource", dataSource);
                    emfp.put("hibernate.connection.datasource", dataSource);
                    LocalContainerEntityManagerFactoryBean factoryBean = builder
                            .dataSource(dataSource)
                            .packages("io.leego.ah.openapi.entity")
                            .persistenceUnit(o.getKey())
                            .properties(emfp)
                            .build();
                    factoryBean.afterPropertiesSet();
                    return new Sync(o.getKey(),
                            Objects.requireNonNull(factoryBean.getObject()).createEntityManager(),
                            Executors.newSingleThreadExecutor());
                })
                .toList();
    }

    @Override
    public void onApplicationEvent(SyncEvent event) {
        if (event.getType() == EventType.INSERT) {
            execute((List<?>) event.getSource(), event.getTag());
        } else if (event.getType() == EventType.UPDATE) {
            execute((List<?>) event.getSource(), event.getTag());
        }
    }

    private void execute(List<?> entities, String tag) {
        if (syncs.isEmpty() || entities.isEmpty()) {
            return;
        }
        for (Sync sync : syncs) {
            final String name = sync.name;
            final EntityManager entityManager = sync.entityManager;
            final ExecutorService executorService = sync.executorService;
            executorService.execute(() -> {
                long begin = System.currentTimeMillis();
                EntityTransaction tx = entityManager.getTransaction();
                tx.begin();
                try {
                    for (Object entity : entities) {
                        entityManager.merge(entity);
                    }
                    entityManager.flush();
                    tx.commit();
                    long end = System.currentTimeMillis();
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

    record Sync(String name, EntityManager entityManager, ExecutorService executorService) {}
}
