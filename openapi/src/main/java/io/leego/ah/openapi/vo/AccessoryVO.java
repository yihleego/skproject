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
public class AccessoryVO {
    private String id;
    private String name;
    private String icon;
    private String location;
    private ColorizationVO[] colorization;
}
