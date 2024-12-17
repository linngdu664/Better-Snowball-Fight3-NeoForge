package com.linngdu664.bsf.entity;

import com.linngdu664.bsf.entity.ai.goal.RegionControllerGolemRangedAttackGoal;
import com.linngdu664.bsf.entity.ai.goal.RegionControllerGolemTargetNearGoal;
import com.linngdu664.bsf.entity.ai.goal.target.RegionControllerGolemHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.RegionControllerGolemNearestAttackableTargetGoal;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class RegionControllerSnowGolemEntity extends AbstractBSFSnowGolemEntity {
    private static final EntityDataAccessor<Byte> FIXED_TEAM_ID = SynchedEntityData.defineId(RegionControllerSnowGolemEntity.class, EntityDataSerializers.BYTE);

    public RegionControllerSnowGolemEntity(EntityType<? extends AbstractBSFSnowGolemEntity> entityType, Level level) {
        super(entityType, level);
        setDropSnowball(false);
        setDropEquipment(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FIXED_TEAM_ID, (byte) -1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("FixedTeamId", getFixedTeamId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setFixedTeamId(pCompound.getByte("FixedTeamId"));
    }

    public byte getFixedTeamId() {
        return entityData.get(FIXED_TEAM_ID);
    }

    public void setFixedTeamId(byte teamId) {
        entityData.set(FIXED_TEAM_ID, teamId);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new RegionControllerGolemTargetNearGoal(this));
        goalSelector.addGoal(4, new RegionControllerGolemRangedAttackGoal(this, 1.0, 30, 50.0F));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8, 1E-5F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 5.0F));
        targetSelector.addGoal(1, new RegionControllerGolemHurtByTargetGoal(this));
        targetSelector.addGoal(2, new RegionControllerGolemNearestAttackableTargetGoal(this));
    }

    @Override
    public void tick() {
        Level level = level();
        if (!level.isClientSide) {
            if (isAlive() && (aliveRange != null && !aliveRange.inRegion(position()) || getFixedTeamId() >= 0 && lifespan > 0 && --lifespan == 0)) {
                hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
            }
        }
        super.tick();
    }

    @Override
    public boolean canPassiveAttackInAttackEnemyTeamMode(Entity entity) {
        if (entity == null) {
            return false;
        }
        BSFTeamSavedData savedData = getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        if (entity instanceof OwnableEntity ownableEntity) {
            return getFixedTeamId() != savedData.getTeam(ownableEntity.getOwnerUUID());
        }
        if (entity instanceof RegionControllerSnowGolemEntity snowGolem) {
            return getFixedTeamId() != snowGolem.getFixedTeamId();
        }
        if (entity.getType().equals(EntityType.PLAYER)) {
            return getFixedTeamId() != savedData.getTeam(entity.getUUID());
        }
        return false;
    }

    @Override
    public boolean shouldConsumeAmmo() {
        return false;
    }

    @Override
    public boolean shouldDamageWeapon() {
        return false;
    }

    @Override
    public boolean canMoveAndAttack() {
        return true;
    }
}
