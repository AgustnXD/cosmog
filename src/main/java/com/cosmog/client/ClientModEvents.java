package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.AdeEntity;
import com.cosmog.entity.AntiRegulatorEntity;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.cosmog.entity.ScreamerEntity;
import com.cosmog.entity.StalkerEntity;
import com.cosmog.entity.TrueAdeEntity;
import com.cosmog.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CosmogMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    private static ResourceLocation tex(String name) {
        return new ResourceLocation(CosmogMod.MODID, "textures/entity/" + name + ".png");
    }

    private static final ResourceLocation DISK_ORANGE = tex("accretion_disk");
    private static final ResourceLocation DISK_WHITE = tex("accretion_disk_white");

    private static StyledHumanoidRenderer<CosmosRegulatorEntity, CrossedArmsModel<CosmosRegulatorEntity>> regulator(EntityRendererProvider.Context ctx) {
        return new StyledHumanoidRenderer<>(ctx, new CrossedArmsModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 1.4F,
                tex("cosmos_regulator"), 2.3F, 2.3F, 2.3F, DISK_ORANGE);
    }

    private static StyledHumanoidRenderer<AntiRegulatorEntity, CrossedArmsModel<AntiRegulatorEntity>> anti(EntityRendererProvider.Context ctx) {
        return new StyledHumanoidRenderer<>(ctx, new CrossedArmsModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 1.4F,
                tex("anti_regulator"), 2.3F, 2.3F, 2.3F, DISK_WHITE);
    }

    private static StyledHumanoidRenderer<AdeEntity, CrossedArmsModel<AdeEntity>> ade(EntityRendererProvider.Context ctx) {
        return new StyledHumanoidRenderer<>(ctx, new CrossedArmsModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5F,
                tex("ade"), 0.93F, 0.93F, 0.93F, DISK_WHITE);
    }

    private static StyledHumanoidRenderer<TrueAdeEntity, TrueAdeModel> trueAde(EntityRendererProvider.Context ctx) {
        return new StyledHumanoidRenderer<>(ctx, new TrueAdeModel(ctx.bakeLayer(TrueAdeModel.LAYER)), 0.5F,
                tex("true_ade"), 1.0F, 1.0F, 1.0F, null);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COSMOS_REGULATOR.get(), ClientModEvents::regulator);
        event.registerEntityRenderer(ModEntities.ANTI_REGULATOR.get(), ClientModEvents::anti);
        event.registerEntityRenderer(ModEntities.ADE.get(), ClientModEvents::ade);
        event.registerEntityRenderer(ModEntities.STALKER.get(), StalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.TRUE_ADE.get(), ClientModEvents::trueAde);
        event.registerEntityRenderer(ModEntities.SCREAMER.get(), ScreamerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AccretionDiskLayer.LAYER, AccretionDiskLayer::createLayer);
        event.registerLayerDefinition(StalkerModel.LAYER, StalkerModel::createLayer);
        event.registerLayerDefinition(ScreamerModel.LAYER, ScreamerModel::createLayer);
        event.registerLayerDefinition(TrueAdeModel.LAYER, TrueAdeModel::createLayer);
    }

    /** Cielo galactico: dimension_type "effects": "cosmog:galaxy" */
    @SubscribeEvent
    public static void registerSky(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(CosmogMod.MODID, "galaxy"), new GalaxyEffects());
    }
}
