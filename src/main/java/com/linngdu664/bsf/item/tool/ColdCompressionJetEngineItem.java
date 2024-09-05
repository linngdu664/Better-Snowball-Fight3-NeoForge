package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.client.screenshake.ScreenshakeInstance;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.ToggleMovingSoundPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.ParticleRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColdCompressionJetEngineItem extends AbstractBSFEnhanceableToolItem {
    public static final int STARTUP_DURATION = 24;

    public ColdCompressionJetEngineItem() {
        super(Rarity.RARE, 400);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        BlockPos blockPos1 = new BlockPos((int) pEntity.getX() - 1, (int) pEntity.getY(), (int) pEntity.getZ() - 1);
        if (!pLevel.isClientSide && (pLevel.getBlockState(blockPos1).is(BlockTags.SNOW) || pLevel.getBlockState(blockPos1.below()).is(BlockTags.SNOW)) && pStack.getDamageValue() > 0 && pLevel.getRandom().nextFloat() < 0.55f) {
            if (pIsSelected) {
                pStack.setDamageValue(Math.max(pStack.getDamageValue() - 2, 0));
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(pEntity, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(pEntity.position().add(-0.5, 0, -0.5), pEntity.position().add(0.5, 0, 0.5), new Vec3(0, 1, 0), 0.1, 0.15, 4), BSFParticleType.SNOWFLAKE.ordinal()));
            } else {
                pStack.setDamageValue(Math.max(pStack.getDamageValue() - 1, 0));
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(pEntity, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(pEntity.position().add(-0.5, 0, -0.5), pEntity.position().add(0.5, 0, 0.5), new Vec3(0, 1, 0), 0.1, 0.15, 2), BSFParticleType.SNOWFLAKE.ordinal()));
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pStack.getDamageValue() == pStack.getMaxDamage() - 1) {
            pLivingEntity.stopUsingItem();
            return;
        }
        int i = this.getUseDuration(pStack, pLivingEntity) - pRemainingUseDuration;
        Vec3 vec3 = Vec3.directionFromRotation(pLivingEntity.getXRot(), pLivingEntity.getYRot());
        Vec3 particlesPos = pLivingEntity.getEyePosition();
        if (!pLevel.isClientSide) {
            if (i == 0) {
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP1.get(), ToggleMovingSoundPayload.PLAY_ONCE));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP2.get(), ToggleMovingSoundPayload.PLAY_LOOP));
            }
            pStack.hurtAndBreak(1, (ServerLevel) pLevel, pLivingEntity, p -> {});
        }
        if (i < STARTUP_DURATION) {
            if (!pLevel.isClientSide) {
                Vec3 newPos = particlesPos.add(vec3.reverse());
                ((ServerLevel) pLevel).sendParticles(ParticleRegister.SHORT_TIME_SNOWFLAKE.get(), newPos.x, newPos.y, newPos.z, 1, 0, 0, 0, 0.04);
            }
            return;
        }
        if (i == STARTUP_DURATION) {
            Vec3 aVec = vec3.scale(2);
            pLivingEntity.push(aVec.x, aVec.y, aVec.z);
            if (!pLevel.isClientSide) {
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP2.get(), ToggleMovingSoundPayload.PLAY_LOOP));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP3.get(), ToggleMovingSoundPayload.PLAY_ONCE));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP4.get(), ToggleMovingSoundPayload.PLAY_LOOP));
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(pLivingEntity, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(particlesPos, vec3.reverse().scale(0.5), 5F, 10, 0.2F, 0), BSFParticleType.SNOWFLAKE.ordinal()));
            } else {
                ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(6)).setIntensity(0.6f).setEasing(Easing.EXPO_IN_OUT));
            }
        } else {
            Vec3 aVec = vec3.scale(0.2);
            pLivingEntity.push(aVec.x, aVec.y, aVec.z);
            List<LivingEntity> list = pLevel.getEntitiesOfClass(LivingEntity.class, pLivingEntity.getBoundingBox().inflate(2), p -> !pLivingEntity.equals(p));
            for (LivingEntity entity : list) {
                if (entity.getTicksFrozen() < 100) {
                    entity.setTicksFrozen(100);
                }
                if (pLivingEntity instanceof Player player) {
                    entity.hurt(pLevel.damageSources().playerAttack(player), Float.MIN_VALUE);
                }
            }
            if (vec3.y > 0) {
                pLivingEntity.resetFallDistance();
            }
        }
        if (!pLevel.isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(pLivingEntity, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(particlesPos, vec3.reverse(), 2F, 60, 0.5F, 0), BSFParticleType.SNOWFLAKE.ordinal()));
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        Level level = entity.level();
        if (!level.isClientSide) {
            PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new ToggleMovingSoundPayload(entity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP5.get(), ToggleMovingSoundPayload.PLAY_ONCE));
            PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new ToggleMovingSoundPayload(entity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP4.get(), ToggleMovingSoundPayload.STOP_LOOP));
            PacketDistributor.sendToPlayersInDimension((ServerLevel) level, new ToggleMovingSoundPayload(entity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP2.get(), ToggleMovingSoundPayload.STOP_LOOP));
        }
        if (entity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.getItem().equals(newStack.getItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("cold_compression_jet_engine.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("cold_compression_jet_engine1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
    }
}
