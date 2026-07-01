package com.linngdu664.bsf.entity.executor;

import com.linngdu664.bsf.entity.Absorbable;
import com.linngdu664.bsf.item.component.RegionData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class AbstractExecutor extends Entity implements Absorbable {
    private static final EntityDataAccessor<Integer> MAX_TIME = SynchedEntityData.defineId(AbstractExecutor.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(AbstractExecutor.class, EntityDataSerializers.INT);
    private RegionData aliveRange = null;

    public AbstractExecutor(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AbstractExecutor(EntityType<?> pEntityType, Level pLevel, int maxTime, RegionData region) {
        this(pEntityType, pLevel);
        entityData.set(MAX_TIME, maxTime);
        this.aliveRange = RegionData.copy(region);
    }

    public int getTimer() {
        return entityData.get(TIMER);
    }

    public int getMaxTime() {
        return entityData.get(MAX_TIME);
    }

    public void setMaxTime(int maxTime) {
        entityData.set(MAX_TIME, maxTime);
    }

    public RegionData getAliveRange() {
        return aliveRange;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TIMER, 0);
        builder.define(MAX_TIME, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(TIMER, input.getIntOr("Timer", 0));
        entityData.set(MAX_TIME, input.getIntOr("MaxTime", 0));
        aliveRange = RegionData.loadFromValueInput("AliveRange", input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Timer", entityData.get(TIMER));
        output.putInt("MaxTime", entityData.get(MAX_TIME));
        if (aliveRange != null) {
            aliveRange.saveToValueOutput("AliveRange", output);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (entityData.get(TIMER).equals(entityData.get(MAX_TIME)) || (aliveRange != null && !aliveRange.inRegion(position()))) {
                discard();
            } else {
                entityData.set(TIMER, entityData.get(TIMER) + 1);
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = 32 * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
