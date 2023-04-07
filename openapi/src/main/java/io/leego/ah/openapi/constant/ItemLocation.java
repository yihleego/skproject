package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum ItemLocation implements CodeEnum<String, String> {
    AURA("AURA", "Aura"),
    ARMOR_BACK("ARMOR_BACK", "Armor Back"),
    ARMOR_ANKLE("ARMOR_ANKLE", "Armor Ankle"),
    ARMOR_REAR("ARMOR_REAR", "Armor Rear"),
    ARMOR_FRONT("ARMOR_FRONT", "Armor Front"),
    HELM_FRONT("HELM_FRONT", "Helm Front"),
    HELM_BACK("HELM_BACK", "Helm Back"),
    HELM_SIDE("HELM_SIDE", "Helm Side"),
    HELM_TOP("HELM_TOP", "Helm Top"),
    ;
    private final String code;
    private final String name;
}
