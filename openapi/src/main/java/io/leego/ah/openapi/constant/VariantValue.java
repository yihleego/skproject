package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum VariantValue implements CodeEnum<String, String> {
    LOW("LOW", "Low"),
    MEDIUM("MEDIUM", "Medium"),
    HIGH("HIGH", "High"),
    VERY_HIGH("VERY_HIGH", "Very High"),
    ULTRA("ULTRA", "Ultra"),
    MAXIMUM("MAXIMUM", "Maximum!"),
    ;
    private final String code;
    private final String name;
}
