package com.cosmog.registry;

import com.cosmog.CosmogMod;
import com.cosmog.world.PoolroomsGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, CosmogMod.MODID);

    /** "type": "cosmog:poolrooms" en data/cosmog/dimension/the_umbra.json */
    public static final RegistryObject<Codec<? extends ChunkGenerator>> POOLROOMS =
            GENERATORS.register("poolrooms", () -> PoolroomsGenerator.CODEC);
}
