package com.linngdu664.bsf.entity.snowball;

import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.entity.Absorbable;
import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.tool.GloveItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ParticleRegister;
import com.linngdu664.bsf.registry.TriggerTypeRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBSFSnowballEntity extends ThrowableItemProjectile implements Absorbable {
    private final BSFSnowballEntityProperties properties;
    protected float particleGenerationStepSize = 0.5F;
    protected float particleGeneratePointOffset;
    protected Vec3 previousTickPosition = new Vec3(Double.NaN, Double.NaN, Double.NaN);
    protected boolean isCaught = false;
    private RegionData aliveRange = null;

    public AbstractBSFSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel, BSFSnowballEntityProperties pProperties) {
        super(pEntityType, pLevel);
        this.properties = pProperties;
    }

    public AbstractBSFSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel, BSFSnowballEntityProperties pProperties) {
        super(pEntityType, pX, pY, pZ, pLevel, Items.SNOWBALL.getDefaultInstance());
        this.properties = pProperties;
        this.setItem(new ItemStack(this.getDefaultItem()));
    }

    public AbstractBSFSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel, BSFSnowballEntityProperties pProperties, RegionData region) {
        super(pEntityType, pX, pY, pZ, pLevel, Items.SNOWBALL.getDefaultInstance());
        this.properties = pProperties;
        this.setItem(new ItemStack(this.getDefaultItem()));
        this.aliveRange = RegionData.copy(region);
    }

    public AbstractBSFSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, LivingEntity pShooter, Level pLevel, BSFSnowballEntityProperties pProperties, RegionData region) {
        super(pEntityType, pShooter, pLevel, Items.SNOWBALL.getDefaultInstance());
        this.properties = pProperties;
        this.setItem(new ItemStack(this.getDefaultItem()));
        this.aliveRange = RegionData.copy(region);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Damage", properties.damage);
        output.putFloat("BlazeDamage", properties.blazeDamage);
        output.putInt("WeaknessTicks", properties.weaknessTicks);
        output.putInt("FrozenTicks", properties.frozenTicks);
        output.putDouble("Punch", properties.punch);
        output.putBoolean("CanBeCaught", properties.canBeCaught);
        output.putInt("LaunchFrom", properties.launchFrom.ordinal());
        output.putFloat("ParticleGenerationStepSize", particleGenerationStepSize);
        output.putFloat("ParticleGenerationPointOffset", particleGeneratePointOffset);
        if (aliveRange != null) {
            aliveRange.saveToValueOutput("AliveRange", output);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        properties.damage = input.getFloatOr("Damage", Float.MIN_NORMAL);
        properties.blazeDamage = input.getFloatOr("BlazeDamage", 3.0F);
        properties.weaknessTicks = input.getIntOr("WeaknessTicks", 0);
        properties.frozenTicks = input.getIntOr("FrozenTicks", 0);
        properties.punch = input.getDoubleOr("Punch", 0.0);
        properties.canBeCaught = input.getBooleanOr("CanBeCaught", true);
        int launchFrom = Mth.clamp(input.getIntOr("LaunchFrom", LaunchFrom.HAND.ordinal()), 0, LaunchFrom.values().length - 1);
        properties.launchFrom = LaunchFrom.values()[launchFrom];
        particleGenerationStepSize = input.getFloatOr("ParticleGenerationStepSize", 0.5F);
        particleGeneratePointOffset = input.getFloatOr("ParticleGenerationPointOffset", 0.0F);
        aliveRange = RegionData.loadFromValueInput("AliveRange", input);
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = ServerConfig.SHOOTING_INERTIA.getConfigValue() ? shooter.getKnownMovement() : new Vec3(0,shooter.getDeltaMovement().y,0);
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, shooter.onGround() ? 0.0 : vec3.y, vec3.z));
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Level level = level();
        if (pResult.getEntity() instanceof LivingEntity entity) {
            // Handling the catch
            if (catchOnGlove(entity)) {
                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), 3, 0, 0, 0, 0.04);
                }
                isCaught = true;
                return;
            }

            // Damage entity
            float hurt = entity instanceof Blaze ? properties.blazeDamage : properties.damage;
            entity.hurt(level.damageSources().thrown(this, this.getOwner()), hurt);

            // Handle frozen and weakness effects
            if (properties.frozenTicks > 0 && !(entity instanceof AbstractBSFSnowGolemEntity) && !(entity instanceof SnowGolem)) {
                if (entity.getTicksFrozen() < properties.frozenTicks) {
                    entity.setTicksFrozen(properties.frozenTicks);
                }
                entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 1));
            }
            if (properties.weaknessTicks > 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, properties.weaknessTicks, 1));
            }

            // Push entity
            if (entity.isPushable()) {
                Vec3 vec3d = this.getDeltaMovement().multiply(0.1 * properties.punch, 0.0, 0.1 * properties.punch);
                entity.push(vec3d.x, 0.0, vec3d.z);
            }
            if (getOwner() instanceof LivingEntity owner) {
                owner.setLastHurtMob(entity);
                if (owner instanceof ServerPlayer serverPlayer) {
                    TriggerTypeRegister.SNOWBALL_DAMAGE_TRIGGER.get().trigger(serverPlayer, this, hurt);
                }
            }
        }
        Vec3 location = BSFCommonUtil.getRealEntityHitPosOnMoveVecWithHitResult(this, pResult);
        spawnBasicParticles(level, location);
        callTraceParticlesEnd(location);
    }

    /**
     * Triggered when an entity hits a block.
     *
     * @param p_37258_ blockHitResult
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        Vec3 location = p_37258_.getLocation();
        spawnBasicParticles(level(), location);
        callTraceParticlesEnd(location);
    }

    /**
     * This method will be called every tick.
     */
    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && aliveRange != null && !aliveRange.inRegion(position())) {
            discard();
        }
        callTraceParticles();
    }

    protected void callTraceParticles() {
        if (!Double.isNaN(previousTickPosition.x)) {
            float v = (float) this.getDeltaMovement().length();
            int n = (int) (v / particleGenerationStepSize);
            int num = 0;
            for (int i = 0; i <= n && particleGeneratePointOffset + i * particleGenerationStepSize < v; i++) {
                generateVelIndependentTraceParticles(this.getPreviousPosition((particleGeneratePointOffset + i * particleGenerationStepSize) / v, previousTickPosition));
                num++;
            }
            particleGeneratePointOffset = num * particleGenerationStepSize + particleGeneratePointOffset - v;
        }
        previousTickPosition = this.getPosition(0);
    }

    protected void callTraceParticlesEnd(Vec3 pos) {
        float v = (float) this.getPosition(1).distanceTo(pos);
        Vec3 vec3d = this.getPosition(1).add(this.getDeltaMovement().normalize().scale(v));
        int n = (int) (v / particleGenerationStepSize);
        for (int i = 0; i <= n && particleGeneratePointOffset + i * particleGenerationStepSize < v; i++) {
            generateVelIndependentTraceParticles(this.getCurrentlyPosition((particleGeneratePointOffset + i * particleGenerationStepSize) / v, vec3d));
        }
    }

    protected void generateVelIndependentTraceParticles(Vec3 vec3) {
        // Spawn trace particles
        Level level = level();
        if (level.isClientSide()) {
            level.addParticle(ParticleRegister.SHORT_TIME_SNOWFLAKE.get(), vec3.x, vec3.y + 0.1, vec3.z, 0, 0, 0);
        }
    }

    public final Vec3 getPreviousPosition(float pPartialTicks, Vec3 previousTickPosition) {
        double d0 = Mth.lerp(pPartialTicks, previousTickPosition.x, this.xo);
        double d1 = Mth.lerp(pPartialTicks, previousTickPosition.y, this.yo);
        double d2 = Mth.lerp(pPartialTicks, previousTickPosition.z, this.zo);
        return new Vec3(d0, d1, d2);
    }

    public final Vec3 getCurrentlyPosition(float pPartialTicks, Vec3 position) {
        double d0 = Mth.lerp(pPartialTicks, this.xo, position.x);
        double d1 = Mth.lerp(pPartialTicks, this.yo, position.y);
        double d2 = Mth.lerp(pPartialTicks, this.zo, position.z);
        return new Vec3(d0, d1, d2);
    }

    /**
     * @param entity The player who is using the glove.
     * @return If the glove catches return true.
     */
    private boolean catchOnGlove(LivingEntity entity) {
        Level level = level();
        if (entity instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            if ((offHand.getItem() instanceof GloveItem && player.getUsedItemHand() == InteractionHand.OFF_HAND ||
                    mainHand.getItem() instanceof GloveItem && player.getUsedItemHand() == InteractionHand.MAIN_HAND) &&
                    player.isUsingItem() && isHeadingToSnowball(player) && canBeCaught()) {
                if (mainHand.getItem() instanceof GloveItem glove) {
                    mainHand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    glove.releaseUsing(mainHand, level, player, 1);
                } else if (offHand.getItem() instanceof GloveItem glove) {
                    offHand.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                    glove.releaseUsing(offHand, level, player, 1);
                }
                if (!level.isClientSide()) {
                    ItemStack stack = new ItemStack(getDefaultItem());
                    if (aliveRange != null) {
                        stack.set(DataComponentRegister.REGION, aliveRange);
                    }
                    player.getInventory().placeItemBackInInventory(stack);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_BREAK, SoundSource.NEUTRAL, 3F, 0.4F / level.getRandom().nextFloat() * 0.4F + 0.8F);
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), 3, 0, 0, 0, 0.04);
                }
                return true;
            }
        }
        return false;
    }

    // Check whether the player can catch the snowball
    private boolean isHeadingToSnowball(Player player) {
        Vec3 speedVec = this.getDeltaMovement().normalize();
        Vec3 cameraVec = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
        return Math.abs(cameraVec.dot(speedVec) + 1.0) < 0.2;
    }

    protected void handleExplosion(float radius, Vec3 location) {
        Level level = level();
        if (level instanceof ServerLevel serverLevel) {
            if (serverLevel.getGameRules().get(GameRules.MOB_GRIEFING) && ServerConfig.EXPLOSIVE_DESTROY.getConfigValue()) {
                level.explode(getOwner(), location.x, location.y, location.z, radius, Level.ExplosionInteraction.TNT);
            } else {
                level.explode(getOwner(), location.x, location.y, location.z, radius, Level.ExplosionInteraction.NONE);
            }
        }

    }

    protected void spawnBasicParticles(Level level, Vec3 location) {
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.ITEM_SNOWBALL, location.x, location.y, location.z, 8, 0, 0, 0, 0);
            ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, location.x, location.y, location.z, 8, 0, 0, 0, 0.04);
        }
    }

    public final RegionData getRegion() {
        return aliveRange;
    }

    public final boolean canBeCaught() {
        return properties.canBeCaught;
    }

    public final float getDamage() {
        return properties.damage;
    }

    public void setDamage(float damage) {
        properties.damage = damage;
    }

    public final float getBlazeDamage() {
        return properties.blazeDamage;
    }

    public void setBlazeDamage(float damage) {
        properties.blazeDamage = damage;
    }

    public final int getWeaknessTicks() {
        return properties.weaknessTicks;
    }

    public final int getFrozenTicks() {
        return properties.frozenTicks;
    }

    public final double getPunch() {
        return properties.punch;
    }

    public final LaunchFrom getLaunchFrom() {
        return properties.launchFrom;
    }

    @Override
    public float getSubspacePower() {
        return 1;
    }

    @Override
    public ItemStack getSnowballItem() {
        return getItem();
    }

    public static class BSFSnowballEntityProperties {
        float damage;
        float blazeDamage;
        int weaknessTicks;
        int frozenTicks;
        double punch;
        boolean canBeCaught;
        LaunchFrom launchFrom;

        public BSFSnowballEntityProperties() {
            damage = Float.MIN_NORMAL;
            blazeDamage = 3;
            weaknessTicks = 0;
            frozenTicks = 0;
            punch = 0;
            canBeCaught = true;
            launchFrom = LaunchFrom.HAND;
        }

        public BSFSnowballEntityProperties basicDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public BSFSnowballEntityProperties basicBlazeDamage(float damage) {
            this.blazeDamage = damage;
            return this;
        }

        public BSFSnowballEntityProperties basicWeaknessTicks(int ticks) {
            this.weaknessTicks = ticks;
            return this;
        }

        public BSFSnowballEntityProperties basicFrozenTicks(int ticks) {
            this.frozenTicks = ticks;
            return this;
        }

        public BSFSnowballEntityProperties basicPunch(double punch) {
            this.punch = punch;
            return this;
        }

        public BSFSnowballEntityProperties canBeCaught(boolean canBeCaught) {
            this.canBeCaught = canBeCaught;
            return this;
        }

        public BSFSnowballEntityProperties applyAdjustment(ILaunchAdjustment adjustment) {
            blazeDamage = adjustment.adjustBlazeDamage(blazeDamage);
            damage = adjustment.adjustDamage(damage);
            frozenTicks = adjustment.adjustFrozenTicks(frozenTicks);
            weaknessTicks = adjustment.adjustWeaknessTicks(weaknessTicks);
            punch = adjustment.adjustPunch(punch);
            launchFrom = adjustment.getLaunchFrom();
            return this;
        }
    }
}
