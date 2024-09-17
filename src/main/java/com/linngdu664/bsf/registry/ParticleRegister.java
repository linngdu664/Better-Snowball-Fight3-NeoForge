package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleRegister {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Main.MODID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHORT_TIME_SNOWFLAKE = PARTICLES.register("short_time_snowflake", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIG_LONG_TIME_SNOWFLAKE = PARTICLES.register("big_long_time_snowflake", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> IMPULSE = PARTICLES.register("impulse", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GENERATOR_FIX = PARTICLES.register("generator_fix", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GENERATOR_PUSH = PARTICLES.register("generator_push", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MONSTER_GRAVITY_EXECUTOR_ASH = PARTICLES.register("monster_gravity_executor_ash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MONSTER_REPULSION_EXECUTOR_ASH = PARTICLES.register("monster_repulsion_executor_ash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PROJECTILE_GRAVITY_EXECUTOR_ASH = PARTICLES.register("projectile_gravity_executor_ash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PROJECTILE_REPULSION_EXECUTOR_ASH = PARTICLES.register("projectile_repulsion_executor_ash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VECTOR_INVERSION_RED = PARTICLES.register("vector_inversion_red", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VECTOR_INVERSION_PURPLE = PARTICLES.register("vector_inversion_purple", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUBSPACE_SNOWBALL_HIT_PARTICLE = PARTICLES.register("subspace_snowball_hit_particle", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUBSPACE_SNOWBALL_ATTACK_TRACE = PARTICLES.register("subspace_snowball_attack_trace", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUBSPACE_SNOWBALL_RELEASE_TRACE = PARTICLES.register("subspace_snowball_release_trace", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> IMPLOSION_SNOWBALL_CANNON = PARTICLES.register("implosion_snowball_cannon", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNOW_GOLEM_EQUIP = PARTICLES.register("snow_golen_equip", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPAWN_SNOW = PARTICLES.register("spawn_snow", () -> new SimpleParticleType(false));

    @SubscribeEvent
    public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(ParticleRegister.SHORT_TIME_SNOWFLAKE.get(), ShortTimeSnowflake.Provider::new);
        particleEngine.register(ParticleRegister.BIG_LONG_TIME_SNOWFLAKE.get(), BigLongTimeSnowflake.Provider::new);
        particleEngine.register(ParticleRegister.IMPULSE.get(), ImpulseParticle.Provider::new);
        particleEngine.register(ParticleRegister.GENERATOR_FIX.get(), GeneratorFix.Provider::new);
        particleEngine.register(ParticleRegister.GENERATOR_PUSH.get(), GeneratorPush.Provider::new);
        particleEngine.register(ParticleRegister.MONSTER_GRAVITY_EXECUTOR_ASH.get(), MonsterGravityExecutorAsh.Provider::new);
        particleEngine.register(ParticleRegister.MONSTER_REPULSION_EXECUTOR_ASH.get(), MonsterRepulsionExecutorAsh.Provider::new);
        particleEngine.register(ParticleRegister.PROJECTILE_GRAVITY_EXECUTOR_ASH.get(), ProjectileGravityExecutorAsh.Provider::new);
        particleEngine.register(ParticleRegister.PROJECTILE_REPULSION_EXECUTOR_ASH.get(), ProjectileRepulsionExecutorAsh.Provider::new);
        particleEngine.register(ParticleRegister.VECTOR_INVERSION_RED.get(), VectorInversionParticle.ProviderRed::new);
        particleEngine.register(ParticleRegister.VECTOR_INVERSION_PURPLE.get(), VectorInversionParticle.ProviderPurple::new);
        particleEngine.register(ParticleRegister.SUBSPACE_SNOWBALL_HIT_PARTICLE.get(), SubspaceSnowballHitParticle.Provider::new);
        particleEngine.register(ParticleRegister.SUBSPACE_SNOWBALL_ATTACK_TRACE.get(), SubspaceSnowballAttackTraceParticle.Provider::new);
        particleEngine.register(ParticleRegister.SUBSPACE_SNOWBALL_RELEASE_TRACE.get(), SubspaceSnowballReleaseTraceParticle.Provider::new);
        particleEngine.register(ParticleRegister.IMPLOSION_SNOWBALL_CANNON.get(), ImplosionSnowballCannonParticle.Provider::new);
        particleEngine.register(ParticleRegister.SNOW_GOLEM_EQUIP.get(), SnowGolemEquipParticle.Provider::new);
        particleEngine.register(ParticleRegister.SPAWN_SNOW.get(), SpawnSnowParticle.Provider::new);
    }
}
