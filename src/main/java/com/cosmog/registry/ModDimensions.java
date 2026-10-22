package com.cosmog.registry;

import com.cosmog.CosmogMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ModDimensions {
    /** La dimension se define por datapack en data/cosmog/dimension/the_cosmog.json */
    public static final ResourceKey<Level> COSMOG =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(CosmogMod.MODID, "the_cosmog"));
}
