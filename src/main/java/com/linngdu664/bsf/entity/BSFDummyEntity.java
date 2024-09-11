package com.linngdu664.bsf.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BSFDummyEntity extends Mob {
    private float damage = 0F;
    private final float[] damages = new float[20];
    private int ptr = 0;
    private static final EntityDataAccessor<Float> DPS = SynchedEntityData.defineId(BSFDummyEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> STYLE = SynchedEntityData.defineId(BSFDummyEntity.class, EntityDataSerializers.BYTE);

    public BSFDummyEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DPS, 0F);
        builder.define(STYLE, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(STYLE, compound.getByte("Style"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("Style", getStyle());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public void setDeltaMovement(Vec3 deltaMovement) {
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        super.actuallyHurt(damageSource, damageAmount);
        if (!damageSource.is(DamageTypes.GENERIC_KILL)) {
            damage = damageAmount;
            setHealth(Float.MAX_VALUE);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            damages[ptr++] = damage;
            if (ptr >= damages.length) {
                ptr = 0;
            }
            if (damage > 0F) {
                float sum = 0F;
                for (float v : damages) {
                    sum += v;
                }
                entityData.set(DPS, sum);
                damage = 0F;
            }
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        Item item = itemStack.getItem();
        if (item.equals(Items.SNOWBALL)) {
            Level level = level();
            if (!level.isClientSide) {
                entityData.set(STYLE, (byte) ((getStyle() + 1) % BSFSnowGolemEntity.STYLE_NUM));
                ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 1, this.getZ(), 20, 0, 0.5, 0, 0.05);
                this.playSound(SoundEvents.SNOW_PLACE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public float getDPS() {
        return entityData.get(DPS);
    }

    public byte getStyle() {
        return entityData.get(STYLE);
    }
}