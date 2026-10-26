package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.ScreamerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScreamerRenderer extends MobRenderer<ScreamerEntity, ScreamerModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CosmogMod.MODID, "textures/entity/screamer.png");

    public ScreamerRenderer(EntityRendererProvider.Context context) {
        super(context, new ScreamerModel(context.bakeLayer(ScreamerModel.LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(ScreamerEntity entity) {
        return TEXTURE;
    }
}
