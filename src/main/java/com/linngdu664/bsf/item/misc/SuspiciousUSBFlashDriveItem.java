package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SuspiciousUSBFlashDriveItem extends Item {
    public SuspiciousUSBFlashDriveItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
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
        stack.consume(1, livingEntity);
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 40;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }
}
