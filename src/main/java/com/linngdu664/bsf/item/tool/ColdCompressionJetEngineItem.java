package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.ScreenshakePayload;
import com.linngdu664.bsf.network.to_client.ToggleMovingSoundPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardConeParticlesParas;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.ParticleRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColdCompressionJetEngineItem extends Item {
    public static final int STARTUP_DURATION = 24;

    public ColdCompressionJetEngineItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).durability(400));
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
        if (!(pLivingEntity instanceof Player) || pStack.getDamageValue() == pStack.getMaxDamage() - 1) {
            pLivingEntity.stopUsingItem();
            return;
        }
        int i = this.getUseDuration(pStack, pLivingEntity) - pRemainingUseDuration;
        Vec3 vec3 = Vec3.directionFromRotation(pLivingEntity.getXRot(), pLivingEntity.getYRot());
        Vec3 particlesPos = pLivingEntity.getEyePosition();
        if (pLevel.isClientSide) {
            if (i == STARTUP_DURATION) {
                Vec3 aVec = vec3.scale(2);
                pLivingEntity.push(aVec.x, aVec.y, aVec.z);
            } else if (i > STARTUP_DURATION) {
                Vec3 aVec = vec3.scale(0.2);
                pLivingEntity.push(aVec.x, aVec.y, aVec.z);
            }
        } else {
            pStack.hurtAndBreak(1, (ServerLevel) pLevel, pLivingEntity, p -> {
            });
            if (i == 0) {
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP1.get(), ToggleMovingSoundPayload.PLAY_ONCE));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP2.get(), ToggleMovingSoundPayload.PLAY_LOOP));
            }
            if (i < STARTUP_DURATION) {
                Vec3 newPos = particlesPos.add(vec3.reverse());
                ((ServerLevel) pLevel).sendParticles(ParticleRegister.SHORT_TIME_SNOWFLAKE.get(), newPos.x, newPos.y, newPos.z, 1, 0, 0, 0, 0.04);
                return;
            }
            if (i == STARTUP_DURATION) {
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP2.get(), ToggleMovingSoundPayload.PLAY_LOOP));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP3.get(), ToggleMovingSoundPayload.PLAY_ONCE));
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pLivingEntity.getId(), SoundRegister.COLD_COMPRESSION_JET_ENGINE_STARTUP4.get(), ToggleMovingSoundPayload.PLAY_LOOP));
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(pLivingEntity, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(particlesPos, vec3.reverse().scale(0.5), 5F, 10, 0.2F, 0), BSFParticleType.SNOWFLAKE.ordinal()));
                PacketDistributor.sendToPlayer((ServerPlayer) pLivingEntity, new ScreenshakePayload(6).setIntensity(0.7F).setEasing(Easing.EXPO_IN_OUT));       // 服务端发包防止其他人抖动，我也不知道为什么会这样
            } else {
                List<LivingEntity> list = pLevel.getEntitiesOfClass(LivingEntity.class, pLivingEntity.getBoundingBox().inflate(2), p -> !pLivingEntity.equals(p));
                for (LivingEntity entity : list) {
                    if (entity.getTicksFrozen() < 100) {
                        entity.setTicksFrozen(100);
                    }
                    entity.hurt(pLevel.damageSources().playerAttack((Player) pLivingEntity), Float.MIN_NORMAL);
                }
                if (vec3.y > 0) {
                    pLivingEntity.resetFallDistance();
                }
            }
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
            if (entity instanceof Player player) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }
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
        tooltipComponents.add(Component.translatable("cold_compression_jet_engine.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("cold_compression_jet_engine1.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
