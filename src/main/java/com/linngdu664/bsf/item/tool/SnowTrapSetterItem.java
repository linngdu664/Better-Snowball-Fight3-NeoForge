package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SnowTrapSetterItem extends AbstractBSFEnhanceableToolItem {
    public SnowTrapSetterItem() {
        super(Rarity.UNCOMMON, 1000);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.NONE);
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!pLevel.getBlockState(blockPos).getBlock().equals(Blocks.SNOW)) {
            return InteractionResult.PASS;
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pLivingEntity instanceof Player player) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(pLevel, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!pLevel.getBlockState(blockPos).getBlock().equals(Blocks.SNOW)) {
                player.stopUsingItem();
            } else if (!pLevel.isClientSide() && pRemainingUseDuration == 1) {
                pLevel.setBlockAndUpdate(blockPos, BlockRegister.SNOW_TRAP.get().defaultBlockState());
                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                pStack.hurtAndBreak(1, player, player.getUsedItemHand());
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(blockPos.getCenter().add(-0.5, -0.4, -0.5), blockPos.getCenter().add(0.5, -0.4, 0.5), new Vec3(0, 1, 0), 0.1, 0.3, 5), BSFParticleType.SNOWFLAKE.ordinal()));
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity livingEntity) {
        return 10;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack pStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("snow_trap_setter.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

