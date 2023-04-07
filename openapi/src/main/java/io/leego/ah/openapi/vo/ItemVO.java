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
public class ItemVO {
    private String id;
    private String name;
    private String group;
    private String icon;
    private String description;
    private Integer star;
    private Boolean level;
    private String location;
    private ColorizationVO[] colorization;
}
