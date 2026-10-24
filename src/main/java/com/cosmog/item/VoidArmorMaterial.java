package com.cosmog.item;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** Armadura del vacio: mismas estadisticas que la astral (3x netherite). */
public class VoidArmorMaterial implements ArmorMaterial {
    public static final VoidArmorMaterial INSTANCE = new VoidArmorMaterial();
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};   // BOOTS, LEGGINGS, CHESTPLATE, HELMET
    private static final int[] DEFENSE = {9, 18, 24, 9};

    private static int index(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
        };
    }

    @Override public int getDurabilityForType(ArmorItem.Type type) { return BASE_DURABILITY[index(type)] * 111; }
    @Override public int getDefenseForType(ArmorItem.Type type) { return DEFENSE[index(type)]; }
    @Override public int getEnchantmentValue() { return 45; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_NETHERITE; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(ModItems.DARK_INGOT.get()); }
    @Override public String getName() { return CosmogMod.MODID + ":void"; }
    @Override public float getToughness() { return 9.0F; }
    @Override public float getKnockbackResistance() { return 0.3F; }
}
