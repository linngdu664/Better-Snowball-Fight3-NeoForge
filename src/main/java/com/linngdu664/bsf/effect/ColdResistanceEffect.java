package com.linngdu664.bsf.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ColdResistanceEffect extends MobEffect {
    public ColdResistanceEffect(MobEffectCategory p_19451_, int p_19452_) {
        super(p_19451_, p_19452_);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        int t = pLivingEntity.getTicksFrozen();
        if (t > 5) {
            pLivingEntity.setTicksFrozen(t - 5);
        } else {
            pLivingEntity.setTicksFrozen(0);
        }
        return true;
    }
}
