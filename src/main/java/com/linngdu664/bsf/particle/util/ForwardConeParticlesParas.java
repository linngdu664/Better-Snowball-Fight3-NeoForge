package com.linngdu664.bsf.particle.util;

import net.minecraft.world.phys.Vec3;

public record ForwardConeParticlesParas(Vec3 eyePos, Vec3 sightVec, float r, float aStep, float rStep, double loweredVision) {
}
