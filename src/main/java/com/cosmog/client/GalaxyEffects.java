package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/** Cielo galactico propio de The Cosmog (cubemap 3x2 generado en textures/environment/galaxy_sky.png). */
public class GalaxyEffects extends DimensionSpecialEffects {
    private static final ResourceLocation SKY =
            new ResourceLocation(CosmogMod.MODID, "textures/environment/galaxy_sky.png");
    private static final float INSET = 0.5F / 1024.0F;   // evita costuras entre caras del atlas

    public GalaxyEffects() {
        super(Float.NaN, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    private static float atlasU(int col, float u) {
        return (col + INSET + u * (1.0F - 2.0F * INSET)) / 3.0F;
    }

    private static float atlasV(int row, float v) {
        return (row + INSET + v * (1.0F - 2.0F * INSET)) / 2.0F;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera,
                             Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SKY);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((ticks + partialTick) * 0.004F));   // deriva muy lenta
        for (int i = 0; i < 6; i++) {
            poseStack.pushPose();
            if (i == 1) poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (i == 2) poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            if (i == 3) poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            if (i == 4) poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            if (i == 5) poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            Matrix4f m = poseStack.last().pose();
            int col = i % 3;
            int row = i / 3;
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(m, -100.0F, -100.0F, -100.0F).uv(atlasU(col, 0.0F), atlasV(row, 0.0F)).color(255, 255, 255, 255).endVertex();
            buffer.vertex(m, -100.0F, -100.0F, 100.0F).uv(atlasU(col, 0.0F), atlasV(row, 1.0F)).color(255, 255, 255, 255).endVertex();
            buffer.vertex(m, 100.0F, -100.0F, 100.0F).uv(atlasU(col, 1.0F), atlasV(row, 1.0F)).color(255, 255, 255, 255).endVertex();
            buffer.vertex(m, 100.0F, -100.0F, -100.0F).uv(atlasU(col, 1.0F), atlasV(row, 0.0F)).color(255, 255, 255, 255).endVertex();
            tesselator.end();
            poseStack.popPose();
        }
        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        return true;
    }
}
