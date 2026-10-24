package com.cosmog.item;

import com.cosmog.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Hoja Eclipse: el doble de dano, 80 total (1 + 3 + 76). */
public class EclipseTier implements Tier {
    public static final EclipseTier INSTANCE = new EclipseTier();

    @Override public int getUses() { return 12186; }
    @Override public float getSpeed() { return 16.0F; }
    @Override public float getAttackDamageBonus() { return 76.0F; }
    @Override public int getLevel() { return 5; }
    @Override public int getEnchantmentValue() { return 60; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(ModItems.DARK_INGOT.get(), ModItems.ASTRAL_INGOT.get()); }
}
