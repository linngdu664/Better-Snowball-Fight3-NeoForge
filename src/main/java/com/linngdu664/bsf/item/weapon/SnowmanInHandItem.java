package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.client.screenshake.ScreenshakeInstance;
import com.linngdu664.bsf.entity.snowball.nomal.SmoothSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.ParticleRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowmanInHandItem extends Item {
    private static final ILaunchAdjustment LAUNCH_ADJUSTMENT = new ILaunchAdjustment() {
        @Override
        public double adjustPunch(double punch) {
            return punch + 1;
        }

        @Override
        public int adjustWeaknessTicks(int weaknessTicks) {
            return weaknessTicks;
        }

        @Override
        public int adjustFrozenTicks(int frozenTicks) {
            return frozenTicks;
        }

        @Override
        public float adjustDamage(float damage) {
            return damage;
        }

        @Override
        public float adjustBlazeDamage(float blazeDamage) {
            return blazeDamage;
        }

        @Override
        public LaunchFrom getLaunchFrom() {
            return LaunchFrom.SNOWMAN_IN_HAND;
        }
    };


    public SnowmanInHandItem() {
        super(new Properties().stacksTo(1).durability(256).rarity(Rarity.EPIC));
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        HitResult hitResult = pLivingEntity.pick(3, 0, false);
        BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
        Block block = pLevel.getBlockState(blockPos).getBlock();

        float pitch = pLivingEntity.getXRot();
        float yaw = pLivingEntity.getYRot();
        Vec3 cameraVec = Vec3.directionFromRotation(pitch, yaw);

        if ((block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == Blocks.POWDER_SNOW)) {//charge
            pStack.setDamageValue(pStack.getDamageValue() - 1);
            if (!pLevel.isClientSide()) {
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                ((ServerLevel) pLevel).sendParticles(ParticleRegister.SHORT_TIME_SNOWFLAKE.get(), pLivingEntity.getX() + cameraVec.x * 0.5, pLivingEntity.getEyeY() + cameraVec.y * 0.5, pLivingEntity.getZ() + cameraVec.z * 0.5, 1, 0, 0, 0, 0.04);
            }
        } else if (pStack.getDamageValue() < pStack.getMaxDamage() - 1) {//attack
            if (pLevel.isClientSide()) {
                pLivingEntity.push(-cameraVec.x * 0.025, -cameraVec.y * 0.025, -cameraVec.z * 0.025);
                ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(1)).setIntensity(0.5f).setEasing(Easing.ELASTIC_IN));
            } else {
                for (int i = 0; i < 3; i++) {
                    SmoothSnowballEntity snowballEntity = new SmoothSnowballEntity(pLivingEntity, pLevel, LAUNCH_ADJUSTMENT, pStack.get(DataComponentRegister.REGION.get()));     // harmless
                    if (pLivingEntity.isShiftKeyDown()) {
                        snowballEntity.shootFromRotation(pLivingEntity, pLivingEntity.getXRot(), pLivingEntity.getYRot(), 0, 1, 10.0F);
                    } else {
                        snowballEntity.shootFromRotation(pLivingEntity, pLivingEntity.getXRot(), pLivingEntity.getYRot(), 0, 1, 15.0F);
                    }
                    pLevel.addFreshEntity(snowballEntity);
                }
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                pStack.hurtAndBreak(1, pLivingEntity, LivingEntity.getSlotForHand(pLivingEntity.getUsedItemHand()));
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.hasEffect(EffectRegister.WEAPON_JAM)) {
            return InteractionResultHolder.fail(stack);
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity livingEntity) {
        return 1200;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Options options = Minecraft.getInstance().options;
        tooltipComponents.add(Component.translatable("snowman_in_hand.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("snowman_in_hand1.tooltip", options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("snowman_in_hand2.tooltip", options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
