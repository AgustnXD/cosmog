package com.cosmog.event;

import com.cosmog.CosmogMod;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.cosmog.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos del bus del mod. */
@Mod.EventBusSubscriber(modid = CosmogMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COSMOS_REGULATOR.get(), CosmosRegulatorEntity.createAttributes().build());
    }
}
