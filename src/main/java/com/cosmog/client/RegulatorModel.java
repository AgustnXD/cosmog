package com.cosmog.client;

import com.cosmog.entity.CosmosRegulatorEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

/** Humanoide con los brazos cruzados (pose de la imagen de referencia) cuando no esta en combate. */
public class RegulatorModel extends HumanoidModel<CosmosRegulatorEntity> {
    public RegulatorModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(CosmosRegulatorEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (!entity.isAggressive()) {
            this.rightArm.xRot = -0.95F;
            this.rightArm.yRot = -0.5F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = -0.95F;
            this.leftArm.yRot = 0.5F;
            this.leftArm.zRot = 0.0F;
        }
    }
}
