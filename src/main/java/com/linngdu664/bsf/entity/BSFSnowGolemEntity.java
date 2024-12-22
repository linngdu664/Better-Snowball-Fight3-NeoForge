package com.linngdu664.bsf.entity;

import com.linngdu664.bsf.entity.ai.goal.*;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemNearsetAttackableTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemOwnerHurtByTargetGoal;
import com.linngdu664.bsf.entity.ai.goal.target.BSFGolemOwnerHurtEnemyTeamGoal;
import com.linngdu664.bsf.item.component.UuidData;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.item.tool.SnowballClampItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.misc.BSFTiers;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.ShowGolemRankScreenPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFEnchantmentHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BSFSnowGolemEntity extends AbstractBSFSnowGolemEntity implements OwnableEntity {
    private static final EntityDataAccessor<Byte> STATUS_FLAG = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> LOCATOR_FLAG = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> POTION_SICKNESS = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Component>> TARGET_NAME = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(BSFSnowGolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private boolean isSpecialMode;

    public BSFSnowGolemEntity(EntityType<? extends AbstractBSFSnowGolemEntity> entityType, Level level) {
        super(entityType, level);
        setDropEquipment(true);
        setDropSnowball(true);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATUS_FLAG, (byte) 0);
        builder.define(LOCATOR_FLAG, (byte) 0);
        builder.define(POTION_SICKNESS, 0);
        builder.define(TARGET_NAME, Optional.empty());
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("Status", getStatus());
        pCompound.putByte("Locator", getLocator());
        pCompound.putInt("PotionSickness", getPotionSickness());
        pCompound.putBoolean("SpecialMode", isSpecialMode);
        if (getOwnerUUID() != null) {
            pCompound.putUUID("Owner", getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setStatus(pCompound.getByte("Status"));
        setLocator(pCompound.getByte("Locator"));
        setPotionSickness(pCompound.getInt("PotionSickness"));
        isSpecialMode = pCompound.getBoolean("SpecialMode");
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(getServer(), s);
        }
        setOwnerUUID(uuid);
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

    public int getPotionSickness() {
        return entityData.get(POTION_SICKNESS);
    }

    public void setPotionSickness(int sickness) {
        entityData.set(POTION_SICKNESS, sickness);
    }

    public boolean isSpecialMode() {
        return isSpecialMode;
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new BSFGolemSitWhenOrderedToGoal(this));
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
                    pPlayer.displayClientMessage(Component.translatable("potionSickness.tip", String.valueOf(getPotionSickness())), false);
                }
            } else if (item.equals(ItemRegister.SNOW_GOLEM_MODE_TWEAKER.get())) {
                int targetMode = itemStack.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
                int statusMode = itemStack.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
                if (targetMode != getLocator()) {
                    setTarget(null);
                }
                setLocator((byte) targetMode);
                setStatus((byte) statusMode);
                pPlayer.displayClientMessage(Component.translatable("import_state.tip"), false);
                Vec3 color = new Vec3(0.5, 1, 0.5);
                PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
                level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item.equals(ItemRegister.TARGET_LOCATOR.get()) && getLocator() == 1) {
                Entity entity = ((ServerLevel) level).getEntity(itemStack.getOrDefault(DataComponentRegister.TARGET_UUID, new UuidData(new UUID(0, 0))).uuid());
                if (entity instanceof LivingEntity livingEntity && entity != this) {
                    pPlayer.displayClientMessage(Component.translatable("snow_golem_locator_tip"), false);
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
                    itemStack.set(DataComponentRegister.SNOW_GOLEM_DATA, getReconstructData());
                    pPlayer.displayClientMessage(Component.translatable("copy.tip"), false);
                } else {
                    setEnhance(!getEnhance());
                    pPlayer.displayClientMessage(Component.translatable("golem_enhance.tip", String.valueOf(getEnhance())), false);
                    Vec3 color = new Vec3(1, 0.8, 0.5);
                    PacketDistributor.sendToPlayersTrackingEntity(this, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(this.getPosition(1).add(-0.5, 0, -0.5), this.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 30), BSFParticleType.SNOW_GOLEM_EQUIP.ordinal()));
                }
                level.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else if (item.equals(ItemRegister.SNOW_GOLEM_CONTAINER.get())) {
                if (!itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
                    itemStack.set(DataComponentRegister.SNOW_GOLEM_DATA, getReconstructData());
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
            } else if (item.equals(Items.BLAZE_ROD) && pPlayer.getAbilities().instabuild) {
                PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new ShowGolemRankScreenPayload(getId(), rank, money, lifespan));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        Level level = level();
        if (!level.isClientSide) {
            if (getPotionSickness() > 0) {
                setPotionSickness(getPotionSickness() - 1);
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

    public CompoundTag getReconstructData() {
        CompoundTag tag1 = new CompoundTag();
        saveWithoutId(tag1);
        tag1.remove("Pos");
        tag1.remove("Motion");
        tag1.remove("UUID");
        return tag1;
    }

    public boolean isEntityHasSameOwner(@Nullable LivingEntity pTarget) {
        if (pTarget == null) {
            return false;
        }
        return pTarget instanceof OwnableEntity ownableEntity && Objects.equals(getOwner(), ownableEntity.getOwner());
    }

    @Override
    public boolean canPassiveAttackInAttackEnemyTeamMode(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }
        BSFTeamSavedData savedData = getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        if (entity instanceof OwnableEntity ownableEntity) {
            if (savedData.getTeam(getOwnerUUID()) < 0) {
                return !Objects.equals(getOwner(), ownableEntity.getOwner());
            }
            return !savedData.isSameTeam(getOwner(), ownableEntity.getOwner());
        }
        if (entity instanceof RegionControllerSnowGolemEntity snowGolem) {
            if (savedData.getTeam(getOwnerUUID()) < 0) {
                return true;
            }
            return savedData.getTeam(getOwnerUUID()) != snowGolem.getFixedTeamId();
        }
        if (entity instanceof Player) {
            return !savedData.isSameTeam(getOwner(), entity);
        }
        return false;
    }

    @Override
    public boolean shouldConsumeAmmo() {
        return !(getAmmo().has(DataComponents.UNBREAKABLE) || getEnhance() || isSpecialMode);
    }

    @Override
    public boolean shouldDamageWeapon() {
        ItemStack weapon = getWeapon();
        float damageChance = 1.0F / (1.0F + EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(this, Enchantments.UNBREAKING), weapon));
        return !(weapon.has(DataComponents.UNBREAKABLE) || getEnhance() || isSpecialMode || getRandom().nextFloat() > damageChance);
    }

    @Override
    public boolean canMoveAndAttack() {
        return getStatus() == 2 || getStatus() == 3;
    }
}
