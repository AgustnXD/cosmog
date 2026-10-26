package com.cosmog.client;

import com.cosmog.CosmogMod;
import com.cosmog.entity.TrueAdeEntity;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * Modelo de True Ade: cuerpo de jugador (brazos delgados, capas de chaqueta y mangas) mas pelo trasero,
 * dos coletas con lazos azules, corbatin, bolso con cadena y arete de estrella. Textura 128x128.
 */
public class TrueAdeModel extends HumanoidModel<TrueAdeEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(CosmogMod.MODID, "true_ade"), "main");

    private final ModelPart jacket;
    private final ModelPart leftSleeve;
    private final ModelPart rightSleeve;
    private final ModelPart pigtailL;
    private final ModelPart pigtailR;

    public TrueAdeModel(ModelPart root) {
        super(root);
        this.jacket = root.getChild("jacket");
        this.leftSleeve = root.getChild("left_sleeve");
        this.rightSleeve = root.getChild("right_sleeve");
        this.pigtailL = this.head.getChild("pigtail_l");
        this.pigtailR = this.head.getChild("pigtail_r");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, true);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        PartDefinition body = root.getChild("body");

        head.addOrReplaceChild("hair_back",
                CubeListBuilder.create().texOffs(64, 0).addBox(-4.5F, -9.0F, 4.4F, 9.0F, 9.0F, 2.0F), PartPose.ZERO);
        head.addOrReplaceChild("pigtail_l",
                CubeListBuilder.create().texOffs(64, 32).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offsetAndRotation(5.4F, -7.0F, 1.0F, 0.0F, 0.0F, -0.12F));
        head.addOrReplaceChild("pigtail_r",
                CubeListBuilder.create().texOffs(64, 32).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offsetAndRotation(-5.4F, -7.0F, 1.0F, 0.0F, 0.0F, 0.12F));
        head.addOrReplaceChild("bow_l",
                CubeListBuilder.create().texOffs(64, 48).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(5.2F, -8.3F, -0.5F, 0.0F, 0.0F, -0.4F));
        head.addOrReplaceChild("bow_r",
                CubeListBuilder.create().texOffs(64, 48).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(-5.2F, -8.3F, -0.5F, 0.0F, 0.0F, 0.4F));
        head.addOrReplaceChild("earring",
                CubeListBuilder.create().texOffs(84, 40).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offset(4.3F, -2.5F, 0.0F));

        body.addOrReplaceChild("hair_back_body",
                CubeListBuilder.create().texOffs(64, 16).addBox(-4.0F, 0.0F, 2.3F, 8.0F, 14.0F, 2.0F), PartPose.ZERO);
        body.addOrReplaceChild("bow_tie",
                CubeListBuilder.create().texOffs(64, 52).addBox(-2.0F, 0.3F, -2.9F, 4.0F, 2.0F, 1.0F), PartPose.ZERO);
        body.addOrReplaceChild("bag",
                CubeListBuilder.create().texOffs(64, 56).addBox(0.0F, 0.0F, -2.5F, 2.0F, 6.0F, 5.0F),
                PartPose.offset(4.4F, 8.0F, 0.0F));
        body.addOrReplaceChild("strap",
                CubeListBuilder.create().texOffs(80, 32).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 15.0F, 1.0F),
                PartPose.offsetAndRotation(-3.0F, 0.3F, -2.5F, 0.0F, 0.0F, -0.66F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), List.of(this.jacket, this.leftSleeve, this.rightSleeve));
    }

    @Override
    public void setupAnim(TrueAdeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean sitting = entity.isInSittingPose();
        this.riding = sitting;
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (sitting) {
            this.head.y += 6.0F;
            this.body.y += 6.0F;
            this.rightArm.y += 6.0F;
            this.leftArm.y += 6.0F;
            this.rightLeg.y += 6.0F;
            this.leftLeg.y += 6.0F;
        } else if (!entity.isAggressive()) {   // manos juntas al frente, como en la ilustracion
            this.rightArm.xRot = -0.55F;
            this.rightArm.yRot = -0.32F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = -0.55F;
            this.leftArm.yRot = 0.32F;
            this.leftArm.zRot = 0.0F;
        }
        float sway = Mth.sin(ageInTicks * 0.08F) * 0.05F + limbSwingAmount * 0.2F * Mth.cos(limbSwing * 0.6F);
        this.pigtailL.zRot = -0.12F + sway;
        this.pigtailR.zRot = 0.12F - sway;
        this.hat.copyFrom(this.head);
        this.jacket.copyFrom(this.body);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
    }
}
