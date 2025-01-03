package com.linngdu664.bsf.entity.snowball.special;

import com.linngdu664.bsf.block.LooseSnowBlock;
import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.network.to_client.ScreenshakePayload;
import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class IcicleSnowballEntity extends AbstractSnowStorageSnowballEntity {
    private static final int TRY_SUMMON_ICICLE_MAX_TIMES = 20;
    private static final int ICICLE_MAX_NUM = 15;
    private static final int TRY_SUMMON_ICICLE_DETECTION_RADIUS = 3;
    private static final float FREEZE_PROPAGATION_RATE = 0.1f;
    private final Icicle[] icicles = new Icicle[ICICLE_MAX_NUM];
    private final ArrayDeque<BlockPos> tmpFreezingBlocks = new ArrayDeque<>();
    private final double freezePrecentage;
    private final int freezeTime;
    private boolean isBuildingIcicle = false;
    private boolean isFreezing = false;
    private int iciclesNum = 0;
    private int initSnowStock = 0;
    private int freezingCount = 0;
    private BlockPos impactPoint;

    public IcicleSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, ServerConfig.ICICLE_SNOWBALL_DURATION.getConfigValue());
        this.freezePrecentage = BSFCommonUtil.randDouble(pLevel.random, 0.6, 0.9);
        this.freezeTime = pLevel.random.nextInt(40, 50);
    }

    public IcicleSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, int snowStock, RegionData region) {
        super(EntityRegister.ICICLE_SNOWBALL.get(), pShooter, pLevel, launchAdjustment, snowStock, ServerConfig.ICICLE_SNOWBALL_DURATION.getConfigValue(), region);
        this.initSnowStock = snowStock;
        this.destroyStepSize = Math.max(snowStock / 60, 1);
        this.freezePrecentage = BSFCommonUtil.randDouble(pLevel.random, 0.6, 0.9);
        this.freezeTime = pLevel.random.nextInt(40, 50);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        startTimingOfDiscard();
    }

    private void handleBuildIcicle(Level level) {
        if (snowStock <= 0) return;
        for (int i = 0; i < iciclesNum; i++) {
            icicles[i].generate(level);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Level level = level();
        if (!level.isClientSide && !isBuildingIcicle) {
            impactPoint = new BlockPos(BSFCommonUtil.vec3ToI(pResult.getLocation()));
            icicleInit(level);
        }
        super.onHitEntity(pResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        Level level = level();
        if (!level.isClientSide && !isBuildingIcicle) {
            if (!posIsLooseSnow(level, result.getBlockPos())) {
                impactPoint = result.getBlockPos();
                icicleInit(level);
            }
        }
        super.onHitBlock(result);
    }

    @Override
    public void tick() {
        Level level = level();
        if (isBuildingIcicle) {
            this.setDeltaMovement(0, 0, 0);
            if (isFreezing) {
                hendleFrozenSpread(level);
            } else {
                handleBuildIcicle(level);
            }
        }
        super.tick();
    }

    private void hendleFrozenSpread(Level level) {
        int freezingSpeed = initSnowStock / freezeTime;
        if (!level.isClientSide) {
            for (int t = 0; t < freezingSpeed && freezingCount < initSnowStock * freezePrecentage && !tmpFreezingBlocks.isEmpty(); t++) {
                BlockPos blockPos = tmpFreezingBlocks.poll();
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                tryAddBlockState(level, x + 1, y, z);
                tryAddBlockState(level, x - 1, y, z);
                tryAddBlockState(level, x, y + 1, z);
                tryAddBlockState(level, x, y - 1, z);
                tryAddBlockState(level, x, y, z + 1);
                tryAddBlockState(level, x, y, z - 1);
                for (int i = -1; i < 2; i += 1) {
                    for (int j = -1; j < 2; j += 1) {
                        for (int k = -1; k < 2; k += 1) {
                            tryAddBlockState(level, x + i, y + j, z + k);
                        }
                    }
                }
            }
        }
    }

    private void tryAddBlockState(Level level, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = level.getBlockState(blockPos);
        if (posIsLooseSnow(level, blockPos) && blockState.getValue(LooseSnowBlock.FROZEN) == 0 && level.random.nextDouble() < FREEZE_PROPAGATION_RATE && freezingCount < initSnowStock * freezePrecentage) {
            tmpFreezingBlocks.offer(blockPos);
            level.setBlockAndUpdate(blockPos, blockState.setValue(LooseSnowBlock.FROZEN, 1));
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundRegister.FREEZING.get(), SoundSource.NEUTRAL, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            freezingCount++;
        }
    }

    private void icicleInit(Level level) {
        List<Player> nearbyPlayers = level.getNearbyPlayers(TargetingConditions.forNonCombat(), null, getBoundingBox().inflate(100));
        for (Player player : nearbyPlayers) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new ScreenshakePayload(20).setEasing(Easing.SINE_IN_OUT).setIntensity(0.5F));
        }
        this.setDeltaMovement(0, 0, 0);
        this.setNoGravity(true);
        RandomSource randomSource = level.random;
        //Determine the direction of the icicle
        //init icicle
        for (int i = 0; i < TRY_SUMMON_ICICLE_MAX_TIMES; i++) {
            double theta = BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
            double phi = Math.acos(BSFCommonUtil.randDouble(randomSource, -1, 1)) - Mth.HALF_PI;
            Vec3 direction = BSFCommonUtil.radRotationToVector(TRY_SUMMON_ICICLE_DETECTION_RADIUS, theta, phi);
            BlockPos blockPos1 = impactPoint.offset(Mth.floor(direction.x), Mth.floor(direction.y), Mth.floor(direction.z));
            if ((level.getBlockState(blockPos1).canBeReplaced() || posIsLooseSnow(level, blockPos1)) && iciclesNum < ICICLE_MAX_NUM) {
                icicles[iciclesNum++] = new Icicle(direction.normalize(), BSFCommonUtil.randDouble(randomSource, 0.3, 1), BSFCommonUtil.randDouble(randomSource, 0.1, 0.2));
            }
        }
        if (iciclesNum == 0 || snowStock <= 0) {
            this.discard();
        }
        isBuildingIcicle = true;
    }


    protected void tryPlaceLooseSnowBlock(Level level, BlockPos blockPos) {
        if (snowStock > 0) {
            placeAndRecordBlock(level, blockPos);
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.SNOW_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        } else {
            if (!level.isClientSide) {
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.getBlock().getName().getString().equals(BlockRegister.LOOSE_SNOW_BLOCK.get().getName().getString()) && blockState.getValue(LooseSnowBlock.FROZEN) != 1) {
                    placeAndRecordBlock(level, blockPos);
                }
                if (!isFreezing) {
                    for (int i = 0; i < 4; i++) {
                        tmpFreezingBlocks.offer(impactPoint);
                    }
                    isFreezing = true;
                }
            }
            if (!inBlockDuration) {
                startTimingOfDiscard();
            }

        }
    }

    @Override
    public float getSubspacePower() {
        return 4F;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.ICICLE_SNOWBALL.get();
    }

    private class Icicle {
        private final double icicleStepSize;
        private final double icicleStepRadius;
        private final ArrayList<IciclePoint> path = new ArrayList<>();
        private Vec3 icicleVec;

        public Icicle(Vec3 icicleVec, double icicleStepSize, double icicleStepRadius) {
            this.icicleVec = icicleVec;
            this.icicleStepSize = icicleStepSize;
            this.icicleStepRadius = icicleStepRadius;
            path.add(new Icicle.IciclePoint(impactPoint.getCenter().add(icicleVec)));
        }

        public void generate(Level level) {
            tryPlaceLooseSnowBlock(level, new BlockPos(BSFCommonUtil.vec3ToI(impactPoint.getCenter().add(icicleVec))));
            icicleVec = icicleVec.add(icicleVec.normalize().scale(icicleStepSize));
            path.add(new Icicle.IciclePoint(impactPoint.getCenter().add(icicleVec)));
            for (IciclePoint iciclePoint : path) {
                iciclePoint.pointGenerate(level);
            }
        }

        private class IciclePoint {
            private final Vec3 point;
            private double radius = 0;

            public IciclePoint(Vec3 point) {
                this.point = point;
            }

            public void pointGenerate(Level level) {
                for (int i = 0; i < Mth.ceil(radius); i++) {
                    Vec3 a = icicleVec.cross(new Vec3(0, 1, 0)).normalize();
                    if (a.lengthSqr() == 0) {
                        a = icicleVec.cross(new Vec3(1, 0, 0)).normalize();
                    }
                    Vec3 b = a.cross(icicleVec).normalize();
                    float x = (float) BSFCommonUtil.randDouble(level.random, 0, 2 * Mth.PI);
                    Vec3 c = a.scale(Mth.cos(x)).add(b.scale(Mth.sin(x))).scale(radius);
                    tryPlaceLooseSnowBlock(level, new BlockPos(BSFCommonUtil.vec3ToI(point.add(c))));
                }
                radius += icicleStepRadius;
            }
        }
    }
}
