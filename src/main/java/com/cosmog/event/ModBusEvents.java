package com.cosmog.event;

import com.cosmog.CosmogMod;
import com.cosmog.entity.AdeEntity;
import com.cosmog.entity.AntiRegulatorEntity;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.cosmog.entity.ScreamerEntity;
import com.cosmog.entity.StalkerEntity;
import com.cosmog.entity.TrueAdeEntity;
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
        event.put(ModEntities.ANTI_REGULATOR.get(), AntiRegulatorEntity.createAttributes().build());
        event.put(ModEntities.ADE.get(), AdeEntity.createAttributes().build());
        event.put(ModEntities.STALKER.get(), StalkerEntity.createAttributes().build());
        event.put(ModEntities.SCREAMER.get(), ScreamerEntity.createAttributes().build());
        event.put(ModEntities.TRUE_ADE.get(), TrueAdeEntity.createAttributes().build());
    }
}
