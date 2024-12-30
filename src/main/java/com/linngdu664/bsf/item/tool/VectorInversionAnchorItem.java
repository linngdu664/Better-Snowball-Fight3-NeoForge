package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.network.to_client.ScreenshakePayload;
import com.linngdu664.bsf.network.to_client.VectorInversionParticlesPayload;
import com.linngdu664.bsf.network.to_client.VelocityInversePayload;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VectorInversionAnchorItem extends AbstractBSFEnhanceableToolItem {
    public VectorInversionAnchorItem() {
        super(Rarity.EPIC, 600);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            List<Entity> list = pLevel.getEntitiesOfClass(Entity.class, pPlayer.getBoundingBox().inflate(10), p -> p.getPosition(1).distanceToSqr(pPlayer.getPosition(1)) < 10 * 10);
            for (Entity entity : list) {
                entity.setDeltaMovement(entity.getDeltaMovement().reverse());
                AABB aabb = entity.getBoundingBox();
                Vec3 center = aabb.getCenter();
                double x = 0.5 * (aabb.maxX - aabb.minX);
                double y = 0.5 * (aabb.maxY - aabb.minY);
                double z = 0.5 * (aabb.maxZ - aabb.minZ);
                ((ServerLevel) pLevel).sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z, (int) (400 * z * x * y), x, y, z, 0.3);
                if (entity instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new VelocityInversePayload());
                    PacketDistributor.sendToPlayer(player, new ScreenshakePayload(5).setEasing(Easing.EXPO_IN_OUT).setIntensity(0.8F));
                }
            }
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(pPlayer, new VectorInversionParticlesPayload(pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), 10, 0.24, 400));
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundRegister.VECTOR_INVERSION.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            itemStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pUsedHand));
        }
        pPlayer.getCooldowns().addCooldown(this, 30);
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("vector_inversion_anchor.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
