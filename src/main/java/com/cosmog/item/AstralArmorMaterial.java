package com.cosmog.item;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Armadura astral = 3x Netherite.
 * Netherite: durabilidad x37, defensa 3/8/6/3 (total 20), dureza 3, resist. empuje 0.1, encantabilidad 15.
 * Astral:    durabilidad x111, defensa 9/24/18/9 (total 60), dureza 9, resist. empuje 0.3, encantabilidad 45.
 * (Ojo: Minecraft limita el atributo de armadura a 30 y la dureza a 20; lo que sobrepase se pierde.)
 */
public class AstralArmorMaterial implements ArmorMaterial {
    public static final AstralArmorMaterial INSTANCE = new AstralArmorMaterial();

    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};   // BOOTS, LEGGINGS, CHESTPLATE, HELMET
    private static final int[] DEFENSE = {9, 18, 24, 9};             // BOOTS, LEGGINGS, CHESTPLATE, HELMET

    private static int index(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
        };
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY[index(type)] * 111;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[index(type)];
    }

    @Override
    public int getEnchantmentValue() {
        return 45;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.ASTRAL_INGOT.get());
    }

    @Override
    public String getName() {
        return CosmogMod.MODID + ":astral";   // textures/models/armor/astral_layer_1.png / _2.png
    }

    @Override
    public float getToughness() {
        return 9.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.3F;
    }
}
