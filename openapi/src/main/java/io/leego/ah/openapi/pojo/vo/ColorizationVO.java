package io.leego.ah.openapi.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorizationVO {
    private Integer color;
    private Float[] range;
    private Float[] offsets;
}
