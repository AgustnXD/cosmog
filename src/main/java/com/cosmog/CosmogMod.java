package com.cosmog;

import com.cosmog.registry.ModBlocks;
import com.cosmog.registry.ModChunkGenerators;
import com.cosmog.registry.ModCreativeTabs;
import com.cosmog.registry.ModEntities;
import com.cosmog.registry.ModItems;
import com.cosmog.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CosmogMod.MODID)
public class CosmogMod {
    public static final String MODID = "cosmog";

    public CosmogMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModChunkGenerators.GENERATORS.register(bus);
    }
}
