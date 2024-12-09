package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.EffectRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SuspiciousUSBFlashDriveItem extends Item {
    public SuspiciousUSBFlashDriveItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide) {
            Vec3 color = new Vec3(0.5, 1, 0.5);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(livingEntity.getPosition(1).add(-0.5, 0, -0.5), livingEntity.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 5), BSFParticleType.SPAWN_SNOW.ordinal()));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(EffectRegister.WALLHACK, 2400, 0));
        livingEntity.playSound(SoundEvents.BEACON_ACTIVATE, 3.0F, 1.0F);
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        stack.consume(1, livingEntity);
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 40;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("suspicious_usb_flash_drive.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("suspicious_usb_flash_drive1.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("suspicious_usb_flash_drive2.tooltip", Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
    }
}
