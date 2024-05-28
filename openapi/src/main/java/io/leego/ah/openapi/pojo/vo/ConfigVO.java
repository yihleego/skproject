package io.leego.ah.openapi.pojo.vo;

import io.leego.ah.openapi.entity.Config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVO {
    private String group;
    private String key;
    private String value;
    private Integer version;

    public static ConfigVO from(Config config) {
        return new ConfigVO(
                config.getGroup(),
                config.getKey(),
                config.getValue(),
                config.getVersion());
    }
}
