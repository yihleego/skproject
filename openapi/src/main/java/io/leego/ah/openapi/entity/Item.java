package io.leego.ah.openapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "item")
public class Item implements Serializable {
    @Id
    private String id;
    /** The item name. */
    private String name;
    /** @see io.leego.ah.openapi.constant.ItemGroup */
    private String group;
    /** The icon file path, located in {@literal Spiral Knights.app/Contents/Resources/java/rsrc/}. */
    private String icon;
    private String description;
    /** The star, it ranges from <code>-1</code> to <code>5</code>, <code>-1</code> means no star. */
    private Integer star;
    /** Whether it is a level item. */
    private Boolean level;
    /**
     * The location only for accessories.
     * @see io.leego.ah.openapi.constant.ItemLocation
     */
    private String location;
    /** Colorization used to recolor the icon if it exists. */
    private String colorization;
    @Column(updatable = false)
    private Instant createdTime;
    private Instant updatedTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class Colorization {
        private Integer color;
        private Float[] range;
        private Float[] offsets;
    }
}
