package com.cosmog.item;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Armadura Eclipse (fusion Astral + Vacio): el DOBLE de proteccion.
 * Defensa 18/48/36/18, dureza 18, durabilidad x222. (Minecraft limita el atributo a 30/20: el resto se compensa con
 * el bonus de set completo en ModEvents: la mitad de dano recibido.)
 */
public class EclipseArmorMaterial implements ArmorMaterial {
    public static final EclipseArmorMaterial INSTANCE = new EclipseArmorMaterial();
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE = {18, 36, 48, 18};

    private static int index(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
        };
    }

    @Override public int getDurabilityForType(ArmorItem.Type type) { return BASE_DURABILITY[index(type)] * 222; }
    @Override public int getDefenseForType(ArmorItem.Type type) { return DEFENSE[index(type)]; }
    @Override public int getEnchantmentValue() { return 60; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_NETHERITE; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(ModItems.DARK_INGOT.get(), ModItems.ASTRAL_INGOT.get()); }
    @Override public String getName() { return CosmogMod.MODID + ":eclipse"; }
    @Override public float getToughness() { return 18.0F; }
    @Override public float getKnockbackResistance() { return 0.6F; }
}
