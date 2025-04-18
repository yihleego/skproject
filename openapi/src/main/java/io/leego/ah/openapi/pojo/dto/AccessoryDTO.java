package io.leego.ah.openapi.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessoryDTO {
    private String id;
    private String name;
    private String location;
}
