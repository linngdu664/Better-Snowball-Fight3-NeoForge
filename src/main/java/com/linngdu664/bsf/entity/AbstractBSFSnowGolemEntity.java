package com.linngdu664.bsf.entity;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.tank.LargeSnowballTankItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.*;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.linngdu664.bsf.util.BSFEnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractBSFSnowGolemEntity extends PathfinderMob implements RangedAttackMob {
    public static final int STYLE_NUM = 9;
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> AMMO = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> CORE = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> WEAPON_ANG = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> STYLE = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> CORE_COOL_DOWN = SynchedEntityData.defineId(AbstractBSFSnowGolemEntity.class, EntityDataSerializers.INT);
    // server only
    protected float launchVelocity;
    protected float launchAccuracy;
    protected double shootX;
    protected double shootY;
    protected double shootZ;
    protected int rank;   // 等级，配合积分器使用
    protected int money;  // 金钱，配合积分器使用
    protected int lifespan;   // boss寿命
    protected boolean dropEquipment;
    protected boolean dropSnowball;
    protected RegionData aliveRange;

    protected AbstractBSFSnowGolemEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WEAPON, ItemStack.EMPTY);
        builder.define(AMMO, ItemStack.EMPTY);
        builder.define(CORE, ItemStack.EMPTY);
        builder.define(WEAPON_ANG, 0);
        builder.define(STYLE, (byte) 0);
        builder.define(CORE_COOL_DOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        Component customName = getCustomName();
        if (customName != null) {
            pCompound.putString("CustomName", customName.getString());
        }
        pCompound.putBoolean("CustomNameVisible", isCustomNameVisible());
        pCompound.put("Weapon", getWeapon().saveOptional(registryAccess()));
        pCompound.put("Ammo", getAmmo().saveOptional(registryAccess()));
        pCompound.put("Core", getCore().saveOptional(registryAccess()));
        pCompound.putByte("Style", getStyle());
        pCompound.putInt("CoreCoolDown", getCoreCoolDown());
        pCompound.putBoolean("DropEquipment", dropEquipment);
        pCompound.putBoolean("DropSnowball", dropSnowball);
        pCompound.putInt("Rank", rank);
        pCompound.putInt("Money", money);
        pCompound.putInt("Lifespan", lifespan);
        if (aliveRange != null) {
            aliveRange.saveToCompoundTag("AliveRange", pCompound);
        }
        if (getTarget() != null) {
            pCompound.putUUID("TargetUUID", getTarget().getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("CustomName")) {
            setCustomName(Component.literal(pCompound.getString("CustomName")));
        }
        if (pCompound.contains("CustomNameVisible")) {
            setCustomNameVisible(pCompound.getBoolean("CustomNameVisible"));
        }
        setWeapon(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Weapon")));
        setAmmo(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Ammo")));
        setCore(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Core")));
        setWeaponAng(pCompound.getInt("WeaponAng"));
        setStyle(pCompound.getByte("Style"));
        setCoreCoolDown(pCompound.getInt("CoreCoolDown"));
        dropEquipment = pCompound.getBoolean("DropEquipment");
        dropSnowball = pCompound.getBoolean("DropSnowball");
        rank = pCompound.getInt("Rank");
        money = pCompound.getInt("Money");
        lifespan = pCompound.getInt("Lifespan");
        aliveRange = RegionData.loadFromCompoundTag("AliveRange", pCompound);
        if (pCompound.contains("TargetUUID") && level() instanceof ServerLevel serverLevel) {
            setTarget((LivingEntity) serverLevel.getEntity(pCompound.getUUID("TargetUUID")));   // check level type to avoid exception in top
        }
    }

    public ItemStack getWeapon() {
        return entityData.get(WEAPON);
    }

    public void setWeapon(ItemStack itemStack) {
        entityData.set(WEAPON, itemStack);
    }

    public ItemStack getAmmo() {
        return entityData.get(AMMO);
    }

    public void setAmmo(ItemStack itemStack) {
        entityData.set(AMMO, itemStack);
    }

    public int getWeaponAng() {
        return entityData.get(WEAPON_ANG);
    }

    public void setWeaponAng(int ang) {
        entityData.set(WEAPON_ANG, ang);
    }

    public ItemStack getCore() {
        return entityData.get(CORE);
    }

    public void setCore(ItemStack itemStack) {
        entityData.set(CORE, itemStack);
    }

    public byte getStyle() {
        return entityData.get(STYLE);
    }

    public void setStyle(byte style) {
        entityData.set(STYLE, style);
    }

    public void setLaunchVelocity(float launchVelocity) {
        this.launchVelocity = launchVelocity;
    }

    public void setLaunchAccuracy(float launchAccuracy) {
        this.launchAccuracy = launchAccuracy;
    }

    public void setShootX(double shootX) {
        this.shootX = shootX;
    }

    public void setShootY(double shootY) {
        this.shootY = shootY;
    }

    public void setShootZ(double shootZ) {
        this.shootZ = shootZ;
    }

    public int getCoreCoolDown() {
        return entityData.get(CORE_COOL_DOWN);
    }

    public void setCoreCoolDown(int coolDown) {
        entityData.set(CORE_COOL_DOWN, coolDown);
    }

    public void setDropEquipment(boolean b) {
        this.dropEquipment = b;
    }

    public void setDropSnowball(boolean b) {
        this.dropSnowball = b;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public void setAliveRange(RegionData region) {
        aliveRange = RegionData.copy(region);
    }
    public RegionData getAliveRange() {
        return aliveRange;
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
            if (shouldConsumeAmmo()) {
                ammo.setDamageValue(ammo.getDamageValue() + 1);
                if (ammo.getDamageValue() == ammo.getMaxDamage()) {
                    ItemStack empty;
                    if (ammo.getItem() instanceof LargeSnowballTankItem) {
                        empty = ItemRegister.LARGE_SNOWBALL_TANK.get().getDefaultInstance();
                    } else {
                        empty = ItemRegister.SNOWBALL_TANK.get().getDefaultInstance();
                    }
                    empty.setDamageValue(empty.getMaxDamage());
                    setAmmo(empty);
                }
            }
            if (i == 0) {
                int aStep = 90;
                if (weaponItem.equals(ItemRegister.POWERFUL_SNOWBALL_CANNON.get()) || weaponItem.equals(ItemRegister.SNOWBALL_SHOTGUN.get())) {
                    aStep = 45;
                }
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(getEyePosition(), new Vec3(shootX, shootY, shootZ), 4.5F, aStep, 1.5F, 0.1F), BSFParticleType.SNOWFLAKE.ordinal()));
                playSound(j == 4 ? SoundRegister.SHOTGUN_FIRE_2.get() : SoundRegister.SNOWBALL_CANNON_SHOOT.get(), 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                if (shouldDamageWeapon()) {
                    weapon.setDamageValue(weapon.getDamageValue() + 1);
                    if (weapon.getDamageValue() == 256) {
                        setWeapon(ItemStack.EMPTY);
                        playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
                    }
                }
                setWeaponAng(360);
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        Level level = level();
        if (!level.isClientSide) {
            Item item = getCore().getItem();
            if (item.equals(ItemRegister.REGENERATION_GOLEM_CORE.get())) {
                resetCoreCoolDown();
            } else if (pSource.getDirectEntity() instanceof Projectile && item.equals(ItemRegister.ENDER_TELEPORTATION_GOLEM_CORE.get()) && getCoreCoolDown() == 0 && canMoveAndAttack()) {
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
    public void aiStep() {
        super.aiStep();
        Level level = level();
        if (!level.isClientSide) {
            if (level.getBiome(blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS)) {
                this.hurt(this.damageSources().onFire(), 1.0F);
            }
            if (!EventHooks.canEntityGrief(level, this)) {
                return;
            }
            BlockState blockState = Blocks.SNOW.defaultBlockState();
            BlockState blockState1 = BlockRegister.CRITICAL_SNOW.get().defaultBlockState();
            for (int i = 0; i < 4; ++i) {
                int j = Mth.floor(getX() + (double) ((float) (i % 2 * 2 - 1) * 0.25F));
                int k = Mth.floor(getY());
                int l = Mth.floor(getZ() + (double) ((float) (i / 2 % 2 * 2 - 1) * 0.25F));
                BlockPos blockPos1 = new BlockPos(j, k, l);
                if (getCore().getItem().equals(ItemRegister.CRITICAL_SNOW_GOLEM_CORE.get())) {
                    if ((level.isEmptyBlock(blockPos1) || level.getBlockState(blockPos1).equals(blockState)) && blockState1.canSurvive(level, blockPos1)) {
                        level.setBlockAndUpdate(blockPos1, blockState1);
                        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos1, GameEvent.Context.of(this, blockState1));
                    }
                } else {
                    if (level.isEmptyBlock(blockPos1) && blockState.canSurvive(level, blockPos1)) {
                        level.setBlockAndUpdate(blockPos1, blockState);
                        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos1, GameEvent.Context.of(this, blockState));
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        Level level = level();
        if (!level.isClientSide) {
            setTicksFrozen(0);
            int coreCooldown = getCoreCoolDown();
            if (getWeaponAng() > 0) {
                setWeaponAng(getWeaponAng() - 60);
            }
            Item core = getCore().getItem();
            if (core.equals(ItemRegister.SWIFTNESS_GOLEM_CORE.get())) {
                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0));
            }
            if (coreCooldown > 0) {
                setCoreCoolDown(coreCooldown - 1);
            } else if (coreCooldown == 0) {
                if (core.equals(ItemRegister.REGENERATION_GOLEM_CORE.get())) {
                    this.heal(0.05f);
                } else if (core.equals(ItemRegister.REPULSIVE_FIELD_GOLEM_CORE.get()) && getTarget() != null) {
                    LivingEntity target = getTarget();
                    List<Projectile> list1 = level.getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(3), p -> !this.equals(p.getOwner()) && BSFCommonUtil.vec3AngleCos(getTarget().getPosition(0).subtract(getPosition(0)), p.getPosition(0).subtract(getPosition(0))) > 0);
                    List<Projectile> list = level.getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(5), p -> !this.equals(p.getOwner()) && BSFCommonUtil.vec3AngleCos(getTarget().getPosition(0).subtract(getPosition(0)), p.getPosition(0).subtract(getPosition(0))) > 0);
                    if (!list1.isEmpty()) {
                        for (Projectile projectile : list) {
                            Vec3 vec3 = projectile.getDeltaMovement();
                            double v2 = vec3.lengthSqr();
                            double sin2Phi = vec3.y * vec3.y / v2;
                            double cosPhi = Math.sqrt(1 - sin2Phi);
                            double theta = Mth.atan2(target.getZ() - getZ(), target.getX() - getX());
                            double sinTheta = Mth.sin((float) theta);
                            double cosTheta = Mth.cos((float) theta);
                            double v = vec3.length();
                            Vec3 vec31 = new Vec3(v * cosTheta * cosPhi, -vec3.y, v * sinTheta * cosPhi);
                            projectile.push(vec31.x - vec3.x, vec31.y - vec3.y, vec31.z - vec3.z);
                            ((ServerLevel) level).sendParticles(ParticleRegister.GENERATOR_PUSH.get(), projectile.getX(), projectile.getY(), projectile.getZ(), 1, 0, 0, 0, 0);
                        }
                        playSound(SoundRegister.FIELD_PUSH.get(), 0.5F, 1.0F / (getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                        resetCoreCoolDown();
                    }
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        // 永远不会掉隐藏的护甲了，同时已经判断掉落gamerule了
        if (dropEquipment) {
            int weaponVanish = EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, Enchantments.VANISHING_CURSE), getWeapon());
            int ammoVanish = EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, Enchantments.VANISHING_CURSE), getAmmo());
            int snowGolemExclusive = EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, BSFEnchantmentHelper.SNOW_GOLEM_EXCLUSIVE), getWeapon());
            if (weaponVanish <= 0 && snowGolemExclusive <= 0) {
                spawnAtLocation(getWeapon());
            }
            if (ammoVanish <= 0) {
                spawnAtLocation(getAmmo());
            }
            spawnAtLocation(getCore());
        }
        if (dropSnowball) {
            spawnAtLocation(new ItemStack(Items.SNOWBALL, getRandom().nextInt(0, 16)));
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    protected int calculateFallDamage(float pDistance, float pDamageMultiplier) {
        return 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNOW_GOLEM_DEATH;
    }

    @Override
    protected float getBlockSpeedFactor() {
        if (level().getBlockState(new BlockPos(BSFCommonUtil.vec3ToI(getPosition(0)))).getBlock().equals(BlockRegister.CRITICAL_SNOW.get())) {
            return 1.0F;
        }
        return super.getBlockSpeedFactor();
    }

    @Override
    protected float getBlockJumpFactor() {
        if (level().getBlockState(new BlockPos(BSFCommonUtil.vec3ToI(getPosition(0)))).getBlock().equals(BlockRegister.CRITICAL_SNOW.get())) {
            return 1.0F;
        }
        return super.getBlockJumpFactor();
    }

    public boolean canStandOn(BlockPos blockPos, Level level) {
        return level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty() &&
                level.getBlockState(blockPos.above()).getCollisionShape(level, blockPos.above()).isEmpty() &&
                level.getBlockState(blockPos.below()).blocksMotion();
    }

    public Vec3 getRandomTeleportPos() {
        Level level = level();
        RandomSource randomSource = getRandom();
        float initTheta = (float) BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
        double golemX = getX();
        double golemY = getY();
        double golemZ = getZ();
        boolean clockwise = randomSource.nextInt(0, 2) == 0;
        for (int r = 20; r >= 4; r--) {
            float step = 1.0F / r;
            for (float theta = 0; theta < 2 * Mth.PI; theta += step) {
                float theta1 = clockwise ? theta : -theta;
                for (float phi = 0; phi <= Mth.PI * 0.5; phi += step) {
                    int x = Mth.floor(golemX + r * Mth.cos(initTheta + theta1) * Mth.cos(phi));
                    int y = Mth.floor(golemY + r * Mth.sin(phi));
                    int y1 = Mth.floor(golemY - r * Mth.sin(phi));
                    int z = Mth.floor(golemZ + r * Mth.sin(initTheta + theta1) * Mth.cos(phi));
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (canStandOn(blockPos, level) && aliveRange.inRegion(blockPos)) {
                        return new Vec3(x, y, z);
                    }
                    blockPos = new BlockPos(x, y1, z);
                    if (canStandOn(blockPos, level) && aliveRange.inRegion(blockPos)) {
                        return new Vec3(x, y1, z);
                    }
                }
            }
        }
        return null;
    }

    public void resetCoreCoolDown() {
        setCoreCoolDown(((SnowGolemCoreItem) getCore().getItem()).getCoolDown());
    }

    public void tpWithParticlesAndResetCD(Vec3 vec3) {
        AABB aabb = getBoundingBox();
        Vec3 center = aabb.getCenter();
        double x = 0.5 * (aabb.maxX - aabb.minX);
        double y = 0.5 * (aabb.maxY - aabb.minY);
        ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 81, x, y, x, 0);
        teleportTo(vec3.x, vec3.y, vec3.z);
        playSound(SoundEvents.ENDERMAN_TELEPORT);
        resetCoreCoolDown();
    }

    public Vec3 getMiddleModelForward(float partialTicks, double degreeOffset) {
        return BSFCommonUtil.radRotationToVector(1, (Mth.lerp(partialTicks, this.yBodyRotO + ((this.yHeadRotO - this.yBodyRotO) * 0.25), this.yBodyRot + ((this.yHeadRot - this.yBodyRot) * 0.25)) + 90 + degreeOffset) * Mth.DEG_TO_RAD, 0);
    }

    public abstract boolean canPassiveAttackInAttackEnemyTeamMode(Entity entity);

    public abstract boolean shouldConsumeAmmo();

    public abstract boolean shouldDamageWeapon();

    public abstract boolean canMoveAndAttack();
}
