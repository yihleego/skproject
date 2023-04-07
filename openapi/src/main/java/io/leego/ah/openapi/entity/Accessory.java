package io.leego.ah.openapi.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@FieldNameConstants
public class Accessory {
    /** The accessory id is the item id */
    private String id;
    /** @see io.leego.ah.openapi.constant.ItemLocation */
    private String location;
}
