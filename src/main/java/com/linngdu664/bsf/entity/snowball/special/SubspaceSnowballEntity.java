package com.linngdu664.bsf.entity.snowball.special;

import com.linngdu664.bsf.entity.Absorbable;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.network.to_client.SubspaceSnowballParticlesPayload;
import com.linngdu664.bsf.network.to_client.SubspaceSnowballReleaseTraceParticlesPayload;
import com.linngdu664.bsf.registry.*;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubspaceSnowballEntity extends AbstractBSFSnowballEntity {
    //    private final ArrayList<ItemStack> itemStackArrayList = new ArrayList<>();
    private final HashMap<Item, Integer> snowballCount = new HashMap<>();
    private boolean release = true;
    private int timer = 0;

    public SubspaceSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new BSFSnowballEntityProperties().canBeCaught(false));
        this.setNoGravity(true);
        this.particleGenerationStepSize = 0.1f;
    }

    public SubspaceSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, boolean release, RegionData region) {
        super(EntityRegister.SUBSPACE_SNOWBALL.get(), pShooter, pLevel, new BSFSnowballEntityProperties().canBeCaught(false).applyAdjustment(launchAdjustment), region);
        this.release = release;
        this.setNoGravity(true);
        if (!release) {
            this.particleGenerationStepSize = 0.1f;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Timer", timer);
        pCompound.putBoolean("Release", release);
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<Item, Integer> entry : snowballCount.entrySet()) {
            tag.putInt(BuiltInRegistries.ITEM.getKey(entry.getKey()).toString(), entry.getValue());
        }
        pCompound.put("Snowballs", tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        timer = pCompound.getInt("Timer");
        release = pCompound.getBoolean("Release");
        CompoundTag tag = pCompound.getCompound("Snowballs");
        Set<String> keys = tag.getAllKeys();
        for (String key : keys) {
            snowballCount.put(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(key)), tag.getInt(key));
        }
    }


    @Override
    public void tick() {
        super.tick();
        Level level = level();
        if (!level.isClientSide) {
            AABB aabb = getBoundingBox().inflate(2.5);
            level.getEntities(this, aabb, p -> p instanceof Absorbable).forEach(p -> {
                Absorbable absorbable = (Absorbable) p;
                if (release) {
                    Item item = absorbable.getSnowballItem().getItem();
                    snowballCount.put(item, snowballCount.getOrDefault(item, 0) + 1);
                }
                ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, p.getX(), p.getY(), p.getZ(), 8, 0, 0, 0, 0.05);
                p.discard();
                if (p instanceof SubspaceSnowballEntity) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY(), this.getZ(), 16, 0, 0, 0, 0.05);
                    this.discard();
                }
                if (!release) {
//                    float damage = getDamage();
//                    setDamage(damage + (damage < 15 ? absorbable.getSubspacePower() : 15 * absorbable.getSubspacePower() / damage));
//                    setBlazeDamage(getBlazeDamage() + (damage < 15 ? absorbable.getSubspacePower() : 15 * absorbable.getSubspacePower() / damage));
                    setDamage(getDamage() + absorbable.getSubspacePower());
                    setBlazeDamage(getBlazeDamage() + absorbable.getSubspacePower());
                    Vec3 vec3 = this.getDeltaMovement().scale(0.05);
                    this.push(vec3.x, vec3.y, vec3.z);
                }
                level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegister.SUBSPACE_SNOWBALL_CUT.get(), SoundSource.PLAYERS, 1.3F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            });
            level.getEntitiesOfClass(Snowball.class, aabb, p -> true).forEach(p -> {
                if (release) {
                    Item item = p.getItem().getItem();
                    snowballCount.put(item, snowballCount.getOrDefault(item, 0) + 1);
                }
                ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, p.getX(), p.getY(), p.getZ(), 8, 0, 0, 0, 0.05);
                p.discard();
                if (!release) {
//                    float damage = getDamage();
//                    setDamage(damage + (damage < 15 ? 1 : 15 / damage));
                    setDamage(getDamage() + 1);
                    setBlazeDamage(getBlazeDamage() + 1);
                    Vec3 vec3 = this.getDeltaMovement().scale(0.05);
                    this.push(vec3.x, vec3.y, vec3.z);
                }
            });
            if (timer == 150) {
                generateItemEntities();
                ((ServerLevel) level).sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY(), this.getZ(), 16, 0, 0, 0, 0.05);
                this.discard();
            }
            timer++;
        }
    }

    protected void generateVelIndependentTraceParticles(Vec3 vec3) {
        // Spawn trace particles
        Level level = level();
        if (!level.isClientSide) {
            if (release) {
                Vec3 deltaMovement = this.getDeltaMovement();
                PacketDistributor.sendToPlayersTrackingEntity(this, new SubspaceSnowballReleaseTraceParticlesPayload(vec3.x, vec3.y, vec3.z, deltaMovement.x, deltaMovement.y, deltaMovement.z));
            } else {
                ((ServerLevel) level).sendParticles(ParticleRegister.SUBSPACE_SNOWBALL_ATTACK_TRACE.get(), vec3.x, vec3.y + 0.1, vec3.z, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        Vec3 location = pResult.getLocation();
        Level level = level();
        if (!level.isClientSide) {
            generateItemEntities();
            if (!release) {
                subspaceRangeDamage(location);
            }
            level.playSound(null, location.x, location.y, location.z, SoundRegister.SUBSPACE_SNOWBALL_ATTACK.get(), SoundSource.PLAYERS, 1.3F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Vec3 location = BSFCommonUtil.getRealEntityHitPosOnMoveVecWithHitResult(this, pResult);
        Level level = level();
        if (!level.isClientSide) {
            if (!release) {
                subspaceRangeDamage(location);
                this.discard();
            }
            level.playSound(null, location.x, location.y, location.z, SoundRegister.SUBSPACE_SNOWBALL_ATTACK.get(), SoundSource.PLAYERS, 0.7F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        }
    }

    private void subspaceRangeDamage(Vec3 location) {
        Level level = level();
        float damage = getDamage();
        float r = damage < 5 ? 2 : damage / 5 + 1;
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(location, location).inflate(r + 3), EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(p -> !p.isInvulnerable()));
        for (LivingEntity entity : list) {
            Vec3 rVec = new Vec3(entity.getX(), (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) * 0.5, entity.getZ()).add(location.reverse());
            float len = (float) rVec.length();
            if (len < r) {
                entity.hurt(level.damageSources().explosion(this, getOwner()), damage / len);
            }
        }
        PacketDistributor.sendToPlayersTrackingEntity(this, new SubspaceSnowballParticlesPayload(location.x, location.y, location.z, r, (int) (25 * r)));
    }

    private void generateItemEntities() {
        Level level = level();
        for (Map.Entry<Item, Integer> entry : snowballCount.entrySet()) {
            Item item = entry.getKey();
            int count = entry.getValue();
            int maxStackSize = item.getDefaultMaxStackSize();
            for (int i = 0; i < count / maxStackSize; i++) {
                ItemStack stack = new ItemStack(item, maxStackSize);
                if (getRegion() != null) {
                    stack.set(DataComponentRegister.REGION.get(), getRegion());
                }
                ItemEntity itemEntity = new ItemEntity(level, getX(), getY(), getZ(), stack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
            ItemStack stack = new ItemStack(item, count % maxStackSize);
            if (getRegion() != null) {
                stack.set(DataComponentRegister.REGION.get(), getRegion());
            }
            ItemEntity itemEntity = new ItemEntity(level, getX(), getY(), getZ(), stack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.SUBSPACE_SNOWBALL.get();
    }
}
