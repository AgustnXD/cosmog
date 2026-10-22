package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Caballero de traje negro con un agujero negro por cabeza. */
public class CosmosRegulatorRenderer extends HumanoidMobRenderer<CosmosRegulatorEntity, RegulatorModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CosmogMod.MODID, "textures/entity/cosmos_regulator.png");

    public CosmosRegulatorRenderer(EntityRendererProvider.Context context) {
        super(context, new RegulatorModel(context.bakeLayer(ModelLayers.ZOMBIE)), 1.4F);
        this.addLayer(new AccretionDiskLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CosmosRegulatorEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(CosmosRegulatorEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.3F, 2.3F, 2.3F);   // 1.95 * 2.3 ~ 4.5 bloques (= hitbox)
    }
}
