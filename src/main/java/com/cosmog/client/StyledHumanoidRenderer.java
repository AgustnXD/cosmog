package com.cosmog.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

/** Renderer humanoide con textura, escala propia y (opcional) disco de acrecion. */
public class StyledHumanoidRenderer<T extends Mob, M extends HumanoidModel<T>> extends HumanoidMobRenderer<T, M> {
    private final ResourceLocation texture;
    private final float sx;
    private final float sy;
    private final float sz;

    public StyledHumanoidRenderer(EntityRendererProvider.Context context, M model, float shadow, ResourceLocation texture,
                                  float sx, float sy, float sz, @Nullable ResourceLocation diskTexture) {
        super(context, model, shadow);
        this.texture = texture;
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        if (diskTexture != null) {
            this.addLayer(new AccretionDiskLayer<T, M>(this, context.getModelSet(), diskTexture));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(this.sx, this.sy, this.sz);
    }
}
