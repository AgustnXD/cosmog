package com.cosmog.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;

/** Humanoide con brazos cruzados cuando esta tranquilo; sentado si es una mascota ordenada a sentarse. */
public class CrossedArmsModel<T extends Mob> extends HumanoidModel<T> {
    public CrossedArmsModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean sitting = entity instanceof TamableAnimal pet && pet.isInSittingPose();
        this.riding = sitting;
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (sitting) {
            this.head.y += 6.0F;
            this.body.y += 6.0F;
            this.rightArm.y += 6.0F;
            this.leftArm.y += 6.0F;
            this.rightLeg.y += 6.0F;
            this.leftLeg.y += 6.0F;
            this.hat.copyFrom(this.head);
        } else if (!entity.isAggressive()) {
            this.rightArm.xRot = -0.95F;
            this.rightArm.yRot = -0.5F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = -0.95F;
            this.leftArm.yRot = 0.5F;
            this.leftArm.zRot = 0.0F;
        }
    }
}
