package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum VariantName implements CodeEnum<String, String> {
    ATTACK_SPEED_INCREASED("ATTACK_SPEED_INCREASED", "Attack Speed Increased"),
    CHARGE_TIME_REDUCTION("CHARGE_TIME_REDUCTION", "Charge Time Reduction"),
    DAMAGE_BONUS_VS_CONSTRUCT("DAMAGE_BONUS_VS_CONSTRUCT", "Damage Bonus vs Construct"),
    DAMAGE_BONUS_VS_GREMLIN("DAMAGE_BONUS_VS_GREMLIN", "Damage Bonus vs Gremlin"),
    DAMAGE_BONUS_VS_FIEND("DAMAGE_BONUS_VS_FIEND", "Damage Bonus vs Fiend"),
    DAMAGE_BONUS_VS_BEAST("DAMAGE_BONUS_VS_BEAST", "Damage Bonus vs Beast"),
    DAMAGE_BONUS_VS_UNDEAD("DAMAGE_BONUS_VS_UNDEAD", "Damage Bonus vs Undead"),
    DAMAGE_BONUS_VS_SLIME("DAMAGE_BONUS_VS_SLIME", "Damage Bonus vs Slime"),
    INCREASED_NORMAL_DEFENSE("INCREASED_NORMAL_DEFENSE", "Increased Normal Defense"),
    INCREASED_PIERCING_DEFENSE("INCREASED_PIERCING_DEFENSE", "Increased Piercing Defense"),
    INCREASED_ELEMENTAL_DEFENSE("INCREASED_ELEMENTAL_DEFENSE", "Increased Elemental Defense"),
    INCREASED_SHADOW_DEFENSE("INCREASED_SHADOW_DEFENSE", "Increased Shadow Defense"),
    INCREASED_STUN_RESISTANCE("INCREASED_STUN_RESISTANCE", "Increased Stun Resistance"),
    INCREASED_FREEZE_RESISTANCE("INCREASED_FREEZE_RESISTANCE", "Increased Freeze Resistance"),
    INCREASED_POISON_RESISTANCE("INCREASED_POISON_RESISTANCE", "Increased Poison Resistance"),
    INCREASED_FIRE_RESISTANCE("INCREASED_FIRE_RESISTANCE", "Increased Fire Resistance"),
    INCREASED_SHOCK_RESISTANCE("INCREASED_SHOCK_RESISTANCE", "Increased Shock Resistance"),
    INCREASED_CURSE_RESISTANCE("INCREASED_CURSE_RESISTANCE", "Increased Curse Resistance"),
    INCREASED_SLEEP_RESISTANCE("INCREASED_SLEEP_RESISTANCE", "Increased Sleep Resistance"),
    ;
    private final String code;
    private final String name;
}
