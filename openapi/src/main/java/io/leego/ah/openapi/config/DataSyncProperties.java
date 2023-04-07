package io.leego.ah.openapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("spring.datasync")
public class DataSyncProperties {
    private Map<String, DataSource> datasource = Collections.emptyMap();

    @Data
    public static class DataSource {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}
