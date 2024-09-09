package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.renderer.entity.*;
import com.linngdu664.bsf.entity.BSFDummyEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.executor.*;
import com.linngdu664.bsf.entity.snowball.force.MonsterGravitySnowballEntity;
import com.linngdu664.bsf.entity.snowball.force.MonsterRepulsionSnowballEntity;
import com.linngdu664.bsf.entity.snowball.force.ProjectileGravitySnowballEntity;
import com.linngdu664.bsf.entity.snowball.force.ProjectileRepulsionSnowballEntity;
import com.linngdu664.bsf.entity.snowball.nomal.*;
import com.linngdu664.bsf.entity.snowball.special.*;
import com.linngdu664.bsf.entity.snowball.tracking.*;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Main.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<BSFSnowGolemEntity>> BSF_SNOW_GOLEM =
            ENTITY_TYPES.register("bsf_snow_golem", () -> EntityType.Builder.of(BSFSnowGolemEntity::new, MobCategory.MISC)
                    .sized(0.7F, 1.9F).clientTrackingRange(8).immuneTo(Blocks.POWDER_SNOW)
                    .build(Main.makeResLoc("bsf_snow_golem").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BSFDummyEntity>> BSF_DUMMY =
            ENTITY_TYPES.register("bsf_dummy", () -> EntityType.Builder.of(BSFDummyEntity::new, MobCategory.MISC)
                    .sized(0.7F, 1.9F).clientTrackingRange(8).build(Main.makeResLoc("bsf_dummy").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractFixedForceExecutor>> MONSTER_GRAVITY_EXECUTOR = executorRegister(MonsterGravityExecutor::new, "monster_gravity_executor", 0.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractFixedForceExecutor>> MONSTER_REPULSION_EXECUTOR = executorRegister(MonsterRepulsionExecutor::new, "monster_repulsion_executor", 0.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractFixedForceExecutor>> PROJECTILE_GRAVITY_EXECUTOR = executorRegister(ProjectileGravityExecutor::new, "projectile_gravity_executor", 0.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractFixedForceExecutor>> PROJECTILE_REPULSION_EXECUTOR = executorRegister(ProjectileRepulsionExecutor::new, "projectile_repulsion_executor", 0.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<BlackHoleExecutor>> BLACK_HOLE_EXECUTOR = executorRegister(BlackHoleExecutor::new, "black_hole_executor", 1.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<PowderExecutor>> POWDER_EXECUTOR = executorRegister(PowderExecutor::new, "powder_executor", 0.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<SmoothSnowballEntity>> SMOOTH_SNOWBALL = snowballRegister(SmoothSnowballEntity::new, "smooth_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<CompactedSnowballEntity>> COMPACTED_SNOWBALL = snowballRegister(CompactedSnowballEntity::new, "compacted_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<GlassSnowballEntity>> GLASS_SNOWBALL = snowballRegister(GlassSnowballEntity::new, "glass_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<StoneSnowballEntity>> STONE_SNOWBALL = snowballRegister(StoneSnowballEntity::new, "stone_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<IceSnowballEntity>> ICE_SNOWBALL = snowballRegister(IceSnowballEntity::new, "ice_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<IronSnowballEntity>> IRON_SNOWBALL = snowballRegister(IronSnowballEntity::new, "iron_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<GoldSnowballEntity>> GOLD_SNOWBALL = snowballRegister(GoldSnowballEntity::new, "gold_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianSnowballEntity>> OBSIDIAN_SNOWBALL = snowballRegister(ObsidianSnowballEntity::new, "obsidian_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveSnowballEntity>> EXPLOSIVE_SNOWBALL = snowballRegister(ExplosiveSnowballEntity::new, "explosive_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<SpectralSnowballEntity>> SPECTRAL_SNOWBALL = snowballRegister(SpectralSnowballEntity::new, "spectral_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<FrozenSnowballEntity>> FROZEN_SNOWBALL = snowballRegister(FrozenSnowballEntity::new, "frozen_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<PowderSnowballEntity>> POWDER_SNOWBALL = snowballRegister(PowderSnowballEntity::new, "powder_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<LightMonsterTrackingSnowballEntity>> LIGHT_MONSTER_TRACKING_SNOWBALL = snowballRegister(LightMonsterTrackingSnowballEntity::new, "light_monster_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<HeavyMonsterTrackingSnowballEntity>> HEAVY_MONSTER_TRACKING_SNOWBALL = snowballRegister(HeavyMonsterTrackingSnowballEntity::new, "heavy_monster_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveMonsterTrackingSnowballEntity>> EXPLOSIVE_MONSTER_TRACKING_SNOWBALL = snowballRegister(ExplosiveMonsterTrackingSnowballEntity::new, "explosive_monster_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<LightPlayerTrackingSnowballEntity>> LIGHT_PLAYER_TRACKING_SNOWBALL = snowballRegister(LightPlayerTrackingSnowballEntity::new, "light_player_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<HeavyPlayerTrackingSnowballEntity>> HEAVY_PLAYER_TRACKING_SNOWBALL = snowballRegister(HeavyPlayerTrackingSnowballEntity::new, "heavy_player_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ExplosivePlayerTrackingSnowballEntity>> EXPLOSIVE_PLAYER_TRACKING_SNOWBALL = snowballRegister(ExplosivePlayerTrackingSnowballEntity::new, "explosive_player_tracking_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<MonsterGravitySnowballEntity>> MONSTER_GRAVITY_SNOWBALL = snowballRegister(MonsterGravitySnowballEntity::new, "monster_gravity_snowball");//name is changed
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileGravitySnowballEntity>> PROJECTILE_GRAVITY_SNOWBALL = snowballRegister(ProjectileGravitySnowballEntity::new, "projectile_gravity_snowball");//name is changed
    public static final DeferredHolder<EntityType<?>, EntityType<MonsterRepulsionSnowballEntity>> MONSTER_REPULSION_SNOWBALL = snowballRegister(MonsterRepulsionSnowballEntity::new, "monster_repulsion_snowball");//name is changed
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileRepulsionSnowballEntity>> PROJECTILE_REPULSION_SNOWBALL = snowballRegister(ProjectileRepulsionSnowballEntity::new, "projectile_repulsion_snowball");//name is changed
    public static final DeferredHolder<EntityType<?>, EntityType<BlackHoleSnowballEntity>> BLACK_HOLE_SNOWBALL = snowballRegister(BlackHoleSnowballEntity::new, "black_hole_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<SubspaceSnowballEntity>> SUBSPACE_SNOWBALL = snowballRegister(SubspaceSnowballEntity::new, "subspace_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<EnderSnowballEntity>> ENDER_SNOWBALL = snowballRegister(EnderSnowballEntity::new, "ender_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<GPSSnowballEntity>> GPS_SNOWBALL = snowballRegister(GPSSnowballEntity::new, "gps_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ExpansionSnowballEntity>> EXPANSION_SNOWBALL = snowballRegister(ExpansionSnowballEntity::new, "expansion_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ReconstructSnowballEntity>> RECONSTRUCT_SNOWBALL = snowballRegister(ReconstructSnowballEntity::new, "reconstruct_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<IcicleSnowballEntity>> ICICLE_SNOWBALL = snowballRegister(IcicleSnowballEntity::new, "icicle_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<CriticalFrozenSnowballEntity>> CRITICAL_FROZEN_SNOWBALL = snowballRegister(CriticalFrozenSnowballEntity::new, "critical_frozen_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<ImpulseSnowballEntity>> IMPULSE_SNOWBALL = snowballRegister(ImpulseSnowballEntity::new, "impulse_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<CherryBlossomSnowballEntity>> CHERRY_BLOSSOM_SNOWBALL = snowballRegister(CherryBlossomSnowballEntity::new, "cherry_blossom_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<GhostSnowballEntity>> GHOST_SNOWBALL = snowballRegister(GhostSnowballEntity::new, "ghost_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<SculkSnowballEntity>> SCULK_SNOWBALL = snowballRegister(SculkSnowballEntity::new, "sculk_snowball");
    public static final DeferredHolder<EntityType<?>, EntityType<DuckSnowballEntity>> DUCK_SNOWBALL = snowballRegister(DuckSnowballEntity::new, "duck_snowball");

    //A tool to register snowball entity
    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> snowballRegister(EntityType.EntityFactory<T> pFactory, String name) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(pFactory, MobCategory.MISC)
                .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                .build(Main.makeResLoc(name).toString()));
    }

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> executorRegister(EntityType.EntityFactory<T> pFactory, String name, float size) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(pFactory, MobCategory.MISC)
                .sized(size, size).updateInterval(10).fireImmune()
                .build(Main.makeResLoc(name).toString()));
    }

    @EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RendererRegister {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(BSF_SNOW_GOLEM.get(), BSFSnowGolemRenderer::new);
            event.registerEntityRenderer(BSF_DUMMY.get(), BSFDummyRenderer::new);
            event.registerEntityRenderer(MONSTER_GRAVITY_EXECUTOR.get(), pContext -> new FixedForceExecutorRenderer(pContext, FixedForceExecutorLayerType.MONSTER_GRAVITY));
            event.registerEntityRenderer(MONSTER_REPULSION_EXECUTOR.get(), pContext -> new FixedForceExecutorRenderer(pContext, FixedForceExecutorLayerType.MONSTER_REPULSION));
            event.registerEntityRenderer(PROJECTILE_GRAVITY_EXECUTOR.get(), pContext -> new FixedForceExecutorRenderer(pContext, FixedForceExecutorLayerType.PROJECTILE_GRAVITY));
            event.registerEntityRenderer(PROJECTILE_REPULSION_EXECUTOR.get(), pContext -> new FixedForceExecutorRenderer(pContext, FixedForceExecutorLayerType.PROJECTILE_REPULSION));
            event.registerEntityRenderer(POWDER_EXECUTOR.get(), NoopRenderer::new);
            event.registerEntityRenderer(BLACK_HOLE_EXECUTOR.get(), BlackHoleExecutorCRenderer::new);
            event.registerEntityRenderer(BLACK_HOLE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(COMPACTED_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ENDER_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EXPLOSIVE_MONSTER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EXPLOSIVE_PLAYER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EXPLOSIVE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(FROZEN_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GLASS_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GOLD_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GPS_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(HEAVY_MONSTER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(HEAVY_PLAYER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ICE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(IRON_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(LIGHT_MONSTER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(LIGHT_PLAYER_TRACKING_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(MONSTER_GRAVITY_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(MONSTER_REPULSION_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(OBSIDIAN_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(POWDER_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(PROJECTILE_GRAVITY_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(PROJECTILE_REPULSION_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(SMOOTH_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(SPECTRAL_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(STONE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(SUBSPACE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(EXPANSION_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(RECONSTRUCT_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ICICLE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(CRITICAL_FROZEN_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(IMPULSE_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(CHERRY_BLOSSOM_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GHOST_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(SCULK_SNOWBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(DUCK_SNOWBALL.get(), ThrownItemRenderer::new);
        }
    }

    @EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class attributeRegister {
        @SubscribeEvent
        public static void onCreateEntityAttribute(EntityAttributeCreationEvent event) {
            event.put(BSF_SNOW_GOLEM.get(), TamableAnimal.createLivingAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.FOLLOW_RANGE, 100.0).add(Attributes.MOVEMENT_SPEED, 0.3).build());
            event.put(BSF_DUMMY.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, Float.MAX_VALUE).add(Attributes.FOLLOW_RANGE, 100.0).add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.KNOCKBACK_RESISTANCE, Double.MAX_VALUE).build());
        }
    }
}

