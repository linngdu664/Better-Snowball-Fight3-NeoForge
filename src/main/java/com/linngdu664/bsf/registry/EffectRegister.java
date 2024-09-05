package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.effect.ColdResistanceEffect;
import com.linngdu664.bsf.effect.WeaponJamEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class EffectRegister {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Main.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> COLD_RESISTANCE = EFFECTS.register("cold_resistance", () -> new ColdResistanceEffect(MobEffectCategory.BENEFICIAL, 0x3498db));
    public static final DeferredHolder<MobEffect, MobEffect> WEAPON_JAM = EFFECTS.register("weapon_jam", () -> new WeaponJamEffect(MobEffectCategory.HARMFUL, 0xffffff));
}
