package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CosmogMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COSMOS_REGULATOR.get(), CosmosRegulatorRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AccretionDiskLayer.LAYER, AccretionDiskLayer::createLayer);
    }

    /** Cielo galactico: dimension_type "effects": "cosmog:galaxy" */
    @SubscribeEvent
    public static void registerSky(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(CosmogMod.MODID, "galaxy"), new GalaxyEffects());
    }
}
