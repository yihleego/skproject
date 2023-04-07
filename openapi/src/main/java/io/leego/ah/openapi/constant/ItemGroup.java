package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum ItemGroup implements CodeEnum<String, String> {
    ACCESSORY("ACCESSORY", "Accessory"),
    ARMOR("ARMOR", "Armor"),
    ARTIFACT("ARTIFACT", "Artifact"),
    BOMB("BOMB", "Bomb"),
    CAPSULE("CAPSULE", "Capsule"),
    COSTUME_ARMOR("COSTUME_ARMOR", "Costume Armor"),
    COSTUME_HELMET("COSTUME_HELMET", "Costume Helmet"),
    COSTUME_SHIELD("COSTUME_SHIELD", "Costume Shield"),
    EQUIPPABLE("EQUIPPABLE", "Equippable"),
    FURNISHING("FURNISHING", "Furnishing"),
    GUILD_UPGRADES("GUILD_UPGRADES", "Guild Upgrades"),
    HANDGUN("HANDGUN", "Handgun"),
    HELMET("HELMET", "Helmet"),
    ITEM("ITEM", "Item"),
    KEY("KEY", "Key"),
    MATERIAL("MATERIAL", "Material"),
    MINERAL("MINERAL", "Mineral"),
    RARITY("RARITY", "Rarity"),
    RECIPE("RECIPE", "Recipe"),
    SHIELD("SHIELD", "Shield"),
    SPRITE_GEAR("SPRITE_GEAR", "Sprite Gear"),
    SWORD("SWORD", "Sword"),
    TICKET("TICKET", "Ticket"),
    TOKEN("TOKEN", "Token"),
    TRINKET("TRINKET", "Trinket"),
    USABLE("USABLE", "Usable"),
    VIAL("VIAL", "Vial"),
    ;
    private final String code;
    private final String name;
}
