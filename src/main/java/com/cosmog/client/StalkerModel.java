package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.StalkerEntity;
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

/** El Acechador: 3,6 bloques, encorvado, brazos larguisimos que arrastran, cabeza que tiembla. */
public class StalkerModel extends EntityModel<StalkerEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(CosmogMod.MODID, "stalker"), "main");

    private final ModelPart root;
    private final ModelPart torso;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightFore;
    private final ModelPart leftFore;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightShin;
    private final ModelPart leftShin;

    public StalkerModel(ModelPart root) {
        this.root = root;
        this.torso = root.getChild("torso");
        this.neck = this.torso.getChild("neck");
        this.head = this.neck.getChild("head");
        this.rightArm = this.torso.getChild("right_arm");
        this.leftArm = this.torso.getChild("left_arm");
        this.rightFore = this.rightArm.getChild("fore");
        this.leftFore = this.leftArm.getChild("fore");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightShin = this.rightLeg.getChild("shin");
        this.leftShin = this.leftLeg.getChild("shin");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition torso = root.addOrReplaceChild("torso",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -16.0F, -1.5F, 7.0F, 16.0F, 3.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition neck = torso.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(0.0F, -16.0F, 0.0F));
        neck.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F),
                PartPose.offset(0.0F, -5.0F, 0.0F));
        for (int side = 0; side < 2; side++) {
            float x = side == 0 ? -4.5F : 4.5F;
            PartDefinition arm = torso.addOrReplaceChild(side == 0 ? "right_arm" : "left_arm",
                    CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F),
                    PartPose.offset(x, -15.0F, 0.0F));
            PartDefinition fore = arm.addOrReplaceChild("fore",
                    CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F),
                    PartPose.offset(0.0F, 15.0F, 0.0F));
            fore.addOrReplaceChild("hand",
                    CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 5.0F, 1.0F),
                    PartPose.offset(0.0F, 15.0F, 0.0F));
            PartDefinition leg = root.addOrReplaceChild(side == 0 ? "right_leg" : "left_leg",
                    CubeListBuilder.create().texOffs(0, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 13.0F, 3.0F),
                    PartPose.offset(side == 0 ? -2.2F : 2.2F, -4.0F, 0.0F));
            PartDefinition shin = leg.addOrReplaceChild("shin",
                    CubeListBuilder.create().texOffs(12, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 13.0F, 3.0F),
                    PartPose.offset(0.0F, 13.0F, 0.0F));
            shin.addOrReplaceChild("foot",
                    CubeListBuilder.create().texOffs(24, 36).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 2.0F, 4.0F),
                    PartPose.offset(0.0F, 13.0F, 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(StalkerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float swing = limbSwing * 0.5F;
        float a = limbSwingAmount;
        float rad = (float) (Math.PI / 180.0D);
        this.torso.xRot = 0.32F + (entity.isFleeing() ? 0.25F : 0.0F);
        this.neck.xRot = 0.2F;
        this.head.yRot = netHeadYaw * rad * 0.6F;
        this.head.xRot = headPitch * rad * 0.6F + 0.25F;
        this.head.zRot = Mth.sin(ageInTicks * 0.31F) * 0.08F + (Mth.sin(ageInTicks * 1.7F) > 0.96F ? 0.28F : 0.0F);   // tics nerviosos

        float sway = Mth.sin(ageInTicks * 0.07F) * 0.05F;
        this.rightArm.xRot = -0.15F + Mth.cos(swing) * 0.7F * a + sway;
        this.leftArm.xRot = -0.15F - Mth.cos(swing) * 0.7F * a - sway;
        this.rightArm.zRot = 0.1F;
        this.leftArm.zRot = -0.1F;
        this.rightFore.xRot = -0.35F - Math.max(0.0F, Mth.cos(swing)) * 0.5F * a;
        this.leftFore.xRot = -0.35F - Math.max(0.0F, -Mth.cos(swing)) * 0.5F * a;

        this.rightLeg.xRot = -Mth.cos(swing) * 1.1F * a;
        this.leftLeg.xRot = Mth.cos(swing) * 1.1F * a;
        this.rightShin.xRot = Math.max(0.0F, Mth.cos(swing)) * 0.9F * a;
        this.leftShin.xRot = Math.max(0.0F, -Mth.cos(swing)) * 0.9F * a;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, consumer, light, overlay, red, green, blue, alpha);
    }
}
