package com.linngdu664.bsf.entity;

import com.linngdu664.bsf.entity.ai.goal.RegionControllerGolemRangedAttackGoal;
import com.linngdu664.bsf.entity.ai.goal.RegionControllerGolemTargetNearGoal;
import com.linngdu664.bsf.entity.ai.goal.target.RegionControllerGolemHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.RegionControllerGolemNearestAttackableTargetGoal;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
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
    public void performRangedAttack(LivingEntity livingEntity, float v) {
        Level level = level();
        ItemStack weapon = getWeapon();
        ItemStack ammo = getAmmo();
        AbstractBSFWeaponItem weaponItem = (AbstractBSFWeaponItem) weapon.getItem();
        if (!ammo.has(DataComponentRegister.AMMO_ITEM) || (((AbstractBSFSnowballItem) ammo.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item()).getTypeFlag() & weaponItem.getTypeFlag()) == 0) {
            return;
        }
        ILaunchAdjustment launchAdjustment = weaponItem.getLaunchAdjustment(1, ammo.getItem());
        int j = weapon.getItem() instanceof SnowballShotgunItem ? 4 : 1;
        for (int i = 0; i < j; i++) {
            if (!ammo.has(DataComponentRegister.AMMO_ITEM)) {
                break;
            }
            AbstractBSFSnowballEntity snowball = ((AbstractBSFSnowballItem) ammo.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item()).getCorrespondingEntity(level, this, launchAdjustment, aliveRange);
            snowball.shoot(shootX, shootY, shootZ, launchVelocity, launchAccuracy);
            level.addFreshEntity(snowball);
            if (i == 0) {
                int aStep = 90;
                if (weaponItem.equals(ItemRegister.POWERFUL_SNOWBALL_CANNON.get()) || weaponItem.equals(ItemRegister.SNOWBALL_SHOTGUN.get())) {
                    aStep = 45;
                }
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(getEyePosition(), new Vec3(shootX, shootY, shootZ), 4.5F, aStep, 1.5F, 0.1F), BSFParticleType.SNOWFLAKE.ordinal()));
                playSound(j == 4 ? SoundRegister.SHOTGUN_FIRE_2.get() : SoundRegister.SNOWBALL_CANNON_SHOOT.get(), 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                setWeaponAng(360);
            }
        }
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
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        Level level = level();
        if (!level.isClientSide) {
            Item item = getCore().getItem();
            if (item.equals(ItemRegister.REGENERATION_GOLEM_CORE.get())) {
                resetCoreCoolDown();
            } else if (pSource.getDirectEntity() instanceof Projectile && item.equals(ItemRegister.ENDER_TELEPORTATION_GOLEM_CORE.get()) && getCoreCoolDown() == 0) {
                Vec3 vec3 = getRandomTeleportPos();
                if (vec3 != null) {
                    tpWithParticlesAndResetCD(vec3);
                    return false;
                }
            }
        }
        return super.hurt(pSource, pAmount);
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
}
