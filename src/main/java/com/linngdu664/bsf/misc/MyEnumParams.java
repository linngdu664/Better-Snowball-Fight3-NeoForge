package com.linngdu664.bsf.misc;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

@SuppressWarnings("unused")
public class MyEnumParams {
    public static final EnumProxy<HumanoidModel.ArmPose> WEAPON_ARM_POSE_ENUM_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) (model, entity, arm) -> {
                // copy bow's
                if (arm == HumanoidArm.RIGHT) {
                    model.rightArm.yRot = -0.1F + model.head.yRot;
                    model.leftArm.yRot = 0.1F + model.head.yRot + 0.4F;
                } else {
                    model.rightArm.yRot = -0.1F + model.head.yRot - 0.4F;
                    model.leftArm.yRot = 0.1F + model.head.yRot;
                }
                model.rightArm.xRot = -Mth.PI / 2 + model.head.xRot;
                model.leftArm.xRot = -Mth.PI / 2 + model.head.xRot;
            });
}
