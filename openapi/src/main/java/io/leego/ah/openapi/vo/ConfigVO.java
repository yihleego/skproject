package io.leego.ah.openapi.vo;

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
}
