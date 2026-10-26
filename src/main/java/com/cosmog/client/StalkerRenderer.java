package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.StalkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StalkerRenderer extends MobRenderer<StalkerEntity, StalkerModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CosmogMod.MODID, "textures/entity/stalker.png");

    public StalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new StalkerModel(context.bakeLayer(StalkerModel.LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(StalkerEntity entity) {
        return TEXTURE;
    }
}
