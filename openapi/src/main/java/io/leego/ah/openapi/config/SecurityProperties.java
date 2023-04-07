package io.leego.ah.openapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("security")
public class SecurityProperties {
    private String secretKey = "123456";
}
