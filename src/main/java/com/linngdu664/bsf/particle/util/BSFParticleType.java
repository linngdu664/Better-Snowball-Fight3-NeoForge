package com.linngdu664.bsf.particle.util;

import com.linngdu664.bsf.registry.ParticleRegister;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public enum BSFParticleType {
    SNOWFLAKE(ParticleTypes.SNOWFLAKE),
    VECTOR_INVERSION_RED(ParticleRegister.VECTOR_INVERSION_RED.get()),
    VECTOR_INVERSION_PURPLE(ParticleRegister.VECTOR_INVERSION_PURPLE.get()),
    SNOW_GOLEM_EQUIP(ParticleRegister.SNOW_GOLEM_EQUIP.get()),
    SPAWN_SNOW(ParticleRegister.SPAWN_SNOW.get());
    private final ParticleOptions particleOptions;

    BSFParticleType(ParticleOptions particleOptions) {
        this.particleOptions = particleOptions;
    }

    public ParticleOptions get() {
        return particleOptions;
    }
}
