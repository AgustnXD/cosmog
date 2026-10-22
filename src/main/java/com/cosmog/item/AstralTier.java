package com.cosmog.item;

import com.cosmog.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Tier de la Espada Astral. Dano total mostrado = 1 (base jugador) + 3 (espada) + 36 = 40. */
public class AstralTier implements Tier {
    public static final AstralTier INSTANCE = new AstralTier();

    @Override public int getUses() { return 6093; }                 // 3x netherite (2031)
    @Override public float getSpeed() { return 12.0F; }
    @Override public float getAttackDamageBonus() { return 36.0F; }
    @Override public int getLevel() { return 4; }
    @Override public int getEnchantmentValue() { return 45; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(ModItems.ASTRAL_INGOT.get()); }
}
