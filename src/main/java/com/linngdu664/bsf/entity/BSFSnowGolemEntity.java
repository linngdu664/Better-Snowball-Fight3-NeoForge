package com.linngdu664.bsf.entity;

import com.linngdu664.bsf.entity.ai.goal.BSFGolemFollowOwnerGoal;
import com.linngdu664.bsf.entity.ai.goal.BSFGolemRandomStrollGoal;
import com.linngdu664.bsf.entity.ai.goal.BSFGolemRangedAttackGoal;
import com.linngdu664.bsf.entity.ai.goal.BSFGolemTargetNearGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemNearsetAttackableTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemOwnerHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemOwnerHurtEnemyTeamGoal;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.IntegerGroupData;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.component.UuidData;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.tank.LargeSnowballTankItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.item.tool.SnowballClampItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.misc.BSFTiers;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.*;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.linngdu664.bsf.util.BSFEnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

// I call this "shit mountain".
public class BSFSnowGolemEntity extends TamableAnimal implements RangedAttackMob {
    public static final int STYLE_NUM = 9;
    private static final EntityDataAccessor<ItemStack> WEAPON = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> AMMO = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> CORE = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> WEAPON_ANG = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> STYLE = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> STATUS_FLAG = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> LOCATOR_FLAG = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> POTION_SICKNESS = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CORE_COOL_DOWN = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENHANCE = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<Component>> TARGET_NAME = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Byte> FIXED_TEAM_ID = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);     // 当此属性不为-1时，为固定队伍，owner无效
    // server only
    private float launchVelocity;
    private float launchAccuracy;
    private double shootX;
    private double shootY;
    private double shootZ;
    private int rank;                    // 等级，配合积分器使用
    private boolean dropEquipment;
    private boolean dropSnowball;
    private RegionData aliveRange;
    /*
     status flag:
     0: standby
     1: follow
     2: follow & attack
     3: patrol & attack
     4: turret

     locator flag:
     0: monster
     1: target locator
     2: enemy player & enemy ownable
     3: all
     */

    public BSFSnowGolemEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WEAPON, ItemStack.EMPTY);
        builder.define(AMMO, ItemStack.EMPTY);
        builder.define(CORE, ItemStack.EMPTY);
        builder.define(WEAPON_ANG, 0);
        builder.define(STYLE, (byte) 0);
        builder.define(STATUS_FLAG, (byte) 0);
        builder.define(LOCATOR_FLAG, (byte) 0);
        builder.define(POTION_SICKNESS, 0);
        builder.define(ENHANCE, false);
        builder.define(CORE_COOL_DOWN, 0);
        builder.define(TARGET_NAME, Optional.empty());
        builder.define(FIXED_TEAM_ID, (byte) -1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("Status", getStatus());
        pCompound.putByte("Locator", getLocator());
        pCompound.put("Weapon", getWeapon().saveOptional(registryAccess()));
        pCompound.put("Ammo", getAmmo().saveOptional(registryAccess()));
        pCompound.put("Core", getCore().saveOptional(registryAccess()));
        pCompound.putByte("Style", getStyle());
        pCompound.putBoolean("Enhance", getEnhance());
        pCompound.putInt("PotionSickness", getPotionSickness());
        pCompound.putInt("CoreCoolDown", getCoreCoolDown());
        pCompound.putBoolean("DropEquipment", dropEquipment);
        pCompound.putBoolean("DropSnowball", dropSnowball);
        pCompound.putByte("FixedTeamId", getFixedTeamId());
        pCompound.putInt("Rank", rank);
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
        setStatus(pCompound.getByte("Status"));
        setLocator(pCompound.getByte("Locator"));
        setWeapon(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Weapon")));
        setAmmo(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Ammo")));
        setCore(ItemStack.parseOptional(registryAccess(), pCompound.getCompound("Core")));
        setWeaponAng(pCompound.getInt("WeaponAng"));
        setStyle(pCompound.getByte("Style"));
        setEnhance(pCompound.getBoolean("Enhance"));
        setPotionSickness(pCompound.getInt("PotionSickness"));
        setCoreCoolDown(pCompound.getInt("CoreCoolDown"));
        dropEquipment = pCompound.getBoolean("DropEquipment");
        dropSnowball = pCompound.getBoolean("DropSnowball");
        setFixedTeamId(pCompound.getByte("FixedTeamId"));
        rank = pCompound.getInt("Rank");
        aliveRange = RegionData.loadFromCompoundTag("AliveRange", pCompound);
        if (pCompound.contains("TargetUUID") && level() instanceof ServerLevel serverLevel) {
            setTarget((LivingEntity) serverLevel.getEntity(pCompound.getUUID("TargetUUID")));   // check level type to avoid exception in top
        }
    }

    public byte getStatus() {
        return entityData.get(STATUS_FLAG);
    }

    public void setStatus(byte status) {
        entityData.set(STATUS_FLAG, status);
    }

    public byte getLocator() {
        return entityData.get(LOCATOR_FLAG);
    }

    public void setLocator(byte locator) {
        entityData.set(LOCATOR_FLAG, locator);
    }

    public Optional<Component> getTargetName() {
        return entityData.get(TARGET_NAME);
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

    public int getPotionSickness() {
        return entityData.get(POTION_SICKNESS);
    }

    public void setPotionSickness(int sickness) {
        entityData.set(POTION_SICKNESS, sickness);
    }

    public boolean getEnhance() {
        return entityData.get(ENHANCE);
    }

    public void setEnhance(boolean enhance0) {
        entityData.set(ENHANCE, enhance0);
    }

    public void setDropEquipment(boolean b) {
        this.dropEquipment = b;
    }

    public void setDropSnowball(boolean b) {
        this.dropSnowball = b;
    }

    public byte getFixedTeamId() {
        return entityData.get(FIXED_TEAM_ID);
    }

    public void setFixedTeamId(byte teamId) {
        entityData.set(FIXED_TEAM_ID, teamId);
    }

    public int getRank() {
        return rank;
    }

    public void setAliveRange(RegionData region) {
        aliveRange = RegionData.copy(region);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2, new BSFGolemTargetNearGoal(this));
        goalSelector.addGoal(3, new BSFGolemFollowOwnerGoal(this, 1.0, 8.0F, 3.0F, 20.0F));
        goalSelector.addGoal(4, new BSFGolemRangedAttackGoal(this, 1.0, 30, 50.0F));
        goalSelector.addGoal(5, new BSFGolemRandomStrollGoal(this, 0.8, 1E-5F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 5.0F));
        targetSelector.addGoal(1, new BSFGolemHurtByTargetGoal(this));
        targetSelector.addGoal(2, new BSFGolemOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(3, new BSFGolemOwnerHurtEnemyTeamGoal(this));
        targetSelector.addGoal(4, new BSFGolemNearsetAttackableTargetGoal(this));
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (!pPlayer.equals(getOwner())) {
            return InteractionResult.PASS;
        }
        Level level = level();
        if (!level.isClientSide) {
            ItemStack itemStack = pPlayer.getItemInHand(pHand);
            Item item = itemStack.getItem();
            if (item instanceof SnowballTankItem && getAmmo().isEmpty()) {
                setAmmo(itemStack.copy());
                if (!pPlayer.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                Vec3 color = new Vec3(1, 0.5, 0.5);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
            } else if ((item instanceof SnowballCannonItem || item instanceof SnowballShotgunItem) && getWeapon().isEmpty()) {
                setWeapon(itemStack.copy());
                if (!pPlayer.getAbilities().instabuild) {
                    if (EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, BSFEnchantmentHelper.SNOW_GOLEM_EXCLUSIVE), itemStack) > 0) {
                        itemStack.hurtAndBreak(10, pPlayer, LivingEntity.getSlotForHand(pPlayer.getUsedItemHand()));
                    } else {
                        itemStack.shrink(1);
                    }
                }
                playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                Vec3 color = new Vec3(0.5, 0.5, 1);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
            } else if (itemStack.isEmpty()) {
                if (pPlayer.isShiftKeyDown()) {
                    if (!getWeapon().isEmpty()) {
                        playSound(SoundEvents.DISPENSER_DISPENSE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    }
                    if (EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, BSFEnchantmentHelper.SNOW_GOLEM_EXCLUSIVE), getWeapon()) <= 0) {
                        pPlayer.getInventory().placeItemBackInInventory(getWeapon(), true);
                    }
                    setWeapon(ItemStack.EMPTY);
                } else {
                    if (!getAmmo().isEmpty()) {
                        playSound(SoundEvents.DISPENSER_DISPENSE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    }
                    pPlayer.getInventory().placeItemBackInInventory(getAmmo(), true);
                    setAmmo(ItemStack.EMPTY);
                }
            } else if (item.equals(ItemRegister.SMOOTH_SNOWBALL.get()) || item.equals(Items.POWDER_SNOW_BUCKET) || item.equals(Items.SNOW_BLOCK) || item.equals(Items.ICE)) {
                if (getPotionSickness() == 0) {
                    itemStack.shrink(1);
                    if (item.equals(ItemRegister.SMOOTH_SNOWBALL.get())) {
                        heal(2);
                        setPotionSickness(20);
                        ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getEyeY(), this.getZ(), 8, 0, 0, 0, 0.04);
                        playSound(SoundEvents.SNOW_BREAK, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    } else if (item.equals(Items.POWDER_SNOW_BUCKET)) {
                        pPlayer.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET, 1), true);
                        heal(8);
                        setPotionSickness(100);
                        ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getEyeY(), this.getZ(), 24, 0, 0, 0, 0.04);
                        playSound(SoundEvents.SNOW_BREAK, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    } else if (item.equals(Items.SNOW_BLOCK)) {
                        heal(5);
                        setPotionSickness(60);
                        ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getEyeY(), this.getZ(), 16, 0, 0, 0, 0.04);
                        playSound(SoundEvents.SNOW_BREAK, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    } else {
                        addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
                        setPotionSickness(60);
                        ((ServerLevel) level).sendParticles(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getEyeY(), this.getZ(), 16, 0, 0, 0, 0.04);
                        playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    }
                    ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 7, 0.4, 0.5, 0.4, 0.05);
                    this.playSound(SoundEvents.SNOW_PLACE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                } else {
                    pPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("potionSickness.tip", null, new Object[]{String.valueOf(getPotionSickness())})), false);
                }
            } else if (item.equals(ItemRegister.SNOW_GOLEM_MODE_TWEAKER.get())) {
                int targetMode = itemStack.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
                int statusMode = itemStack.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
                if (targetMode != getLocator()) {
                    setTarget(null);
                }
                setLocator((byte) targetMode);
                setStatus((byte) statusMode);
                setOrderedToSit(statusMode == 0);
                pPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("import_state.tip", null, new Object[0])), false);
                Vec3 color = new Vec3(0.5, 1, 0.5);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
                level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item.equals(ItemRegister.TARGET_LOCATOR.get()) && getLocator() == 1) {
                Entity entity = ((ServerLevel) level).getEntity(itemStack.getOrDefault(DataComponentRegister.TARGET_UUID, new UuidData(new UUID(0, 0))).uuid());
                if (entity instanceof LivingEntity livingEntity && entity != this) {
                    pPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("snow_golem_locator_tip", null, new Object[0])), false);
                    setTarget(livingEntity);
                }
                Vec3 color = new Vec3(0.5, 1, 1);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
                level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item instanceof SnowballClampItem snowballClamp) {
                if (snowballClamp.getTier().equals(BSFTiers.EMERALD)) {
                    pPlayer.getInventory().placeItemBackInInventory(ItemRegister.DUCK_SNOWBALL.get().getDefaultInstance(), true);
                } else {
                    pPlayer.getInventory().placeItemBackInInventory(ItemRegister.SMOOTH_SNOWBALL.get().getDefaultInstance(), true);
                }
                itemStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pHand));
            } else if (item.equals(Items.SNOWBALL)) {
                setStyle((byte) ((getStyle() + 1) % STYLE_NUM));
                ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 1, this.getZ(), 20, 0, 0.5, 0, 0.05);
                this.playSound(SoundEvents.SNOW_PLACE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item.equals(ItemRegister.CREATIVE_SNOW_GOLEM_TOOL.get())) {
                if (pPlayer.isShiftKeyDown()) {
                    CompoundTag tag1 = new CompoundTag();
                    addAdditionalSaveData(tag1);
                    itemStack.set(DataComponentRegister.SNOW_GOLEM_DATA, tag1);
                    pPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("copy.tip", null, new Object[0])), false);
                } else {
                    setEnhance(!getEnhance());
                    pPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("golem_enhance.tip", null, new Object[]{String.valueOf(getEnhance())})), false);
                    Vec3 color = new Vec3(1, 0.8, 0.5);
                    PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
                }
                level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item.equals(ItemRegister.SNOW_GOLEM_CONTAINER.get())) {
                if (!itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
                    CompoundTag tag1 = new CompoundTag();
                    addAdditionalSaveData(tag1);
                    itemStack.set(DataComponentRegister.SNOW_GOLEM_DATA, tag1);
                    playSound(SoundEvents.SNOW_BREAK);
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 1, this.getZ(), 20, 0, 0.5, 0, 0.05);
                    discard();
                }
            } else if (item instanceof SnowGolemCoreItem && getCore().isEmpty()) {
                ItemStack itemStack1 = itemStack.copy();
                itemStack1.setCount(1);
                setCore(itemStack1);
                resetCoreCoolDown();
                if (!pPlayer.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                Vec3 color = new Vec3(0.9, 0.4, 0.9);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
            } else if (item.equals(ItemRegister.SNOW_GOLEM_CORE_REMOVER.get())) {
                if (!getCore().isEmpty()) {
                    playSound(SoundEvents.DISPENSER_DISPENSE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                }
                pPlayer.getInventory().placeItemBackInInventory(getCore(), true);
                setCore(ItemStack.EMPTY);
            } else if (item.equals(ItemRegister.VALUE_ADJUSTMENT_TOOL.get())) {
                rank = itemStack.getOrDefault(DataComponentRegister.INTEGER_GROUP.get(), IntegerGroupData.EMPTY).val1();
                pPlayer.displayClientMessage(Component.literal("Set rank to " + rank), false);
            }
        }
        return InteractionResult.SUCCESS;
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
            if (aliveRange != null && !aliveRange.inRegion(position())) {
                hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
            }
            setTicksFrozen(0);
            if (getEnhance()) {
                heal(1);
                addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, 3));
                if (getCoreCoolDown() > 0) {
                    setCoreCoolDown(Math.max(getCoreCoolDown() - 5, 0));
                }
            }
            if (getPotionSickness() > 0) {
                setPotionSickness(getPotionSickness() - 1);
            }
            if (getWeaponAng() > 0) {
                setWeaponAng(getWeaponAng() - 60);
            }
            Item item = getCore().getItem();
            if (item.equals(ItemRegister.SWIFTNESS_GOLEM_CORE.get())) {
                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0));
            }
            if (getCoreCoolDown() > 0) {
                setCoreCoolDown(getCoreCoolDown() - 1);
            } else if (getCoreCoolDown() == 0) {
                if (item.equals(ItemRegister.REGENERATION_GOLEM_CORE.get())) {
                    this.heal(0.05f);
                } else if (item.equals(ItemRegister.REPULSIVE_FIELD_GOLEM_CORE.get()) && getTarget() != null) {
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
            LivingEntity target = getTarget();
            if (target == null) {
                entityData.set(TARGET_NAME, Optional.empty());
            } else {
                entityData.set(TARGET_NAME, Optional.of(target.getName()));
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
            } else if (pSource.getDirectEntity() instanceof Projectile && item.equals(ItemRegister.ENDER_TELEPORTATION_GOLEM_CORE.get()) && getCoreCoolDown() == 0 && (getStatus() == 2 || getStatus() == 3)) {
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
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pDistanceFactor) {
        Level level = level();
        ItemStack weapon = getWeapon();
        ItemStack ammo = getAmmo();
//        CompoundTag compoundTag = ammo.getOrCreateTag();
        AbstractBSFWeaponItem weaponItem = (AbstractBSFWeaponItem) weapon.getItem();
        if (!ammo.has(DataComponentRegister.AMMO_ITEM) || (((AbstractBSFSnowballItem) ammo.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item()).getTypeFlag() & weaponItem.getTypeFlag()) == 0) {
            return;
        }
        float damageChance = 1.0F / (1.0F + EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, Enchantments.UNBREAKING), weapon));
        ILaunchAdjustment launchAdjustment = weaponItem.getLaunchAdjustment(1, ammo.getItem());
        int j = weapon.getItem() instanceof SnowballShotgunItem ? 4 : 1;
        for (int i = 0; i < j; i++) {
            if (!ammo.has(DataComponentRegister.AMMO_ITEM)) {
                break;
            }
            AbstractBSFSnowballEntity snowball = ((AbstractBSFSnowballItem) ammo.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item()).getCorrespondingEntity(level, this, launchAdjustment, aliveRange);
            snowball.shoot(shootX, shootY, shootZ, launchVelocity, launchAccuracy);
            level.addFreshEntity(snowball);
            if (!getEnhance() && getOwner() != null) {
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
                if (getRandom().nextFloat() <= damageChance && !getEnhance()) {
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
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);
        if (level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
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
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
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

    @Override
    public boolean wantsToAttack(@Nullable LivingEntity pTarget, @NotNull LivingEntity pOwner) {
        if (pTarget == null) {
            return false;
        }
        return !(pTarget instanceof OwnableEntity ownableEntity && pOwner.equals(ownableEntity.getOwner()));
    }

    public boolean canPassiveAttackInAttackEnemyTeamMode(Entity entity) {
        if (entity == null) {
            return false;
        }
        BSFTeamSavedData savedData = getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        int fixedTeamId = getFixedTeamId();
        if (fixedTeamId >= 0) {
            // 确定一点：只有玩家的UUID会出现在队伍存档数据中
            if (entity instanceof BSFSnowGolemEntity snowGolem) {
                if (snowGolem.getFixedTeamId() >= 0) {
                    return fixedTeamId != snowGolem.getFixedTeamId();
                }
                return fixedTeamId != savedData.getTeam(snowGolem.getOwnerUUID());
            }
            if (entity instanceof OwnableEntity ownableEntity) {
                return fixedTeamId != savedData.getTeam(ownableEntity.getOwnerUUID());
            }
            if (entity.getType().equals(EntityType.PLAYER)) {
                return fixedTeamId != savedData.getTeam(entity.getUUID());
            }
            return false;
        }
        if (entity instanceof BSFSnowGolemEntity snowGolem) {
            if (savedData.getTeam(getOwnerUUID()) < 0) {
                return !Objects.equals(getOwner(), snowGolem.getOwner());
            }
            if (snowGolem.getFixedTeamId() >= 0) {
                return savedData.getTeam(getOwnerUUID()) != snowGolem.getFixedTeamId();
            }
            return !savedData.isSameTeam(getOwner(), snowGolem.getOwner());
        }
        if (entity instanceof OwnableEntity ownableEntity) {
            if (savedData.getTeam(getOwnerUUID()) < 0) {
                return !Objects.equals(getOwner(), ownableEntity.getOwner());
            }
            return !savedData.isSameTeam(getOwner(), ownableEntity.getOwner());
        }
        if (entity.getType().equals(EntityType.PLAYER)) {
            return !savedData.isSameTeam(getOwner(), entity);
        }
        return false;
//        if (entity.getType().equals(EntityType.PLAYER)) {
//            return !savedData.isSameTeam(getOwner(), entity);
//        } else if (entity instanceof OwnableEntity ownableEntity) {
//            if (savedData.getTeam(getOwnerUUID()) < 0) {
//                return !Objects.equals(getOwner(), ownableEntity.getOwner());
//            }
//            return !savedData.isSameTeam(getOwner(), ownableEntity.getOwner());
//        } else {
//            return false;
//        }
    }

    public void resetCoreCoolDown() {
        setCoreCoolDown(((SnowGolemCoreItem) getCore().getItem()).getCoolDown());
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
                    if (canStandOn(blockPos, level)) {
                        return new Vec3(x, y, z);
                    }
                    blockPos = new BlockPos(x, y1, z);
                    if (canStandOn(blockPos, level)) {
                        return new Vec3(x, y1, z);
                    }
                }
            }
        }
        return null;
    }

    public boolean canStandOn(BlockPos blockPos, Level level) {
        return level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty() &&
                level.getBlockState(blockPos.above()).getCollisionShape(level, blockPos.above()).isEmpty() &&
                level.getBlockState(blockPos.below()).blocksMotion();
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
}
