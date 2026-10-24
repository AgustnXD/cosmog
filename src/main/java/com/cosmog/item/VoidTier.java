package com.cosmog.item;

import com.cosmog.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Espada del vacio: 40 de dano total (1 + 3 + 36), igual que la astral. */
public class VoidTier implements Tier {
    public static final VoidTier INSTANCE = new VoidTier();

    @Override public int getUses() { return 6093; }
    @Override public float getSpeed() { return 12.0F; }
    @Override public float getAttackDamageBonus() { return 36.0F; }
    @Override public int getLevel() { return 4; }
    @Override public int getEnchantmentValue() { return 45; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(ModItems.DARK_INGOT.get()); }
}
