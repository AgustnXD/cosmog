package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.CosmosRegulatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Disco de acrecion luminoso que gira alrededor de la cabeza-agujero negro del jefe. */
public class AccretionDiskLayer extends RenderLayer<CosmosRegulatorEntity, RegulatorModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(CosmogMod.MODID, "accretion_disk"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CosmogMod.MODID, "textures/entity/accretion_disk.png");

    private final ModelPart disk;

    public AccretionDiskLayer(RenderLayerParent<CosmosRegulatorEntity, RegulatorModel> parent, EntityModelSet models) {
        super(parent);
        this.disk = models.bakeLayer(LAYER).getChild("disk");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("disk",
                CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -0.5F, -12.0F, 24.0F, 1.0F, 24.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 32);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CosmosRegulatorEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);
        this.disk.yRot = ageInTicks * 0.04F;   // gira sobre su propio eje
        this.disk.zRot = -0.32F;               // inclinado como en la imagen
        this.disk.xRot = 0.12F;
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(TEXTURE));
        this.disk.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
