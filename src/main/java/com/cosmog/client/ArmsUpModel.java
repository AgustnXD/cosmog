package com.cosmog.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/** Humanoide con los brazos extendidos hacia delante y temblorosos (Screamer). */
public class ArmsUpModel<T extends Mob> extends HumanoidModel<T> {
    public ArmsUpModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float shake = Mth.sin(ageInTicks * 0.9F) * 0.12F;
        this.rightArm.xRot = -1.45F + shake;
        this.leftArm.xRot = -1.45F - shake;
        this.rightArm.yRot = -0.15F;
        this.leftArm.yRot = 0.15F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
    }
}
