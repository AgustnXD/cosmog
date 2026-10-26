package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.ScreamerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** El Griton: delgado, brazos largos hacia delante y una mandibula desencajada que se abre y cierra. */
public class ScreamerModel extends EntityModel<ScreamerEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(CosmogMod.MODID, "screamer"), "main");

    private final ModelPart root;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ScreamerModel(ModelPart root) {
        this.root = root;
        this.torso = root.getChild("torso");
        this.head = this.torso.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.rightArm = this.torso.getChild("right_arm");
        this.leftArm = this.torso.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition torso = root.addOrReplaceChild("torso",
                CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -12.0F, -1.5F, 6.0F, 12.0F, 3.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition head = torso.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F),
                PartPose.offset(0.0F, -12.0F, 0.0F));
        head.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 3.0F));
        torso.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F),
                PartPose.offset(-4.0F, -11.0F, 0.0F));
        torso.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F),
                PartPose.offset(4.0F, -11.0F, 0.0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(50, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(-1.8F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(50, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(1.8F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(ScreamerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float rad = (float) (Math.PI / 180.0D);
        this.torso.xRot = 0.25F;
        this.head.yRot = netHeadYaw * rad * 0.7F;
        this.head.xRot = headPitch * rad * 0.7F - 0.1F;
        this.jaw.xRot = 0.35F + Math.abs(Mth.sin(ageInTicks * 0.45F)) * 0.6F;   // grito constante
        float shake = Mth.sin(ageInTicks * 1.1F) * 0.1F;
        this.rightArm.xRot = -1.45F + shake;
        this.leftArm.xRot = -1.45F - shake;
        this.rightArm.zRot = 0.12F;
        this.leftArm.zRot = -0.12F;
        float swing = limbSwing * 0.9F;
        this.rightLeg.xRot = Mth.cos(swing) * 1.3F * limbSwingAmount;
        this.leftLeg.xRot = -Mth.cos(swing) * 1.3F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, consumer, light, overlay, red, green, blue, alpha);
    }
}
