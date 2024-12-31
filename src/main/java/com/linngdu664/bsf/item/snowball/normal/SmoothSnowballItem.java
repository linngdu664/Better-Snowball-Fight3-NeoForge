package com.linngdu664.bsf.item.snowball.normal;

import com.linngdu664.bsf.entity.snowball.nomal.SmoothSnowballEntity;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmoothSnowballItem extends AbstractBSFSnowballItem implements ProjectileItem {
    public SmoothSnowballItem() {
        super(Rarity.COMMON, new SnowballProperties().allowLaunchTypeFlag(HAND_TYPE_FLAG));
        DispenserBlock.registerProjectileBehavior(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pUsedHand == InteractionHand.MAIN_HAND) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                SmoothSnowballEntity snowballEntity = new SmoothSnowballEntity(pPlayer, pLevel, getLaunchAdjustment(getSnowballDamageRate(pPlayer)), itemStack.get(DataComponentRegister.REGION.get()));
                snowballEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.25F * getSnowballSlowdownRate(pPlayer), 1.0F);
                pLevel.addFreshEntity(snowballEntity);
            }
            if (!pPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        } else if (pPlayer.getMainHandItem().isEmpty()) {
            ItemStack stack = new ItemStack(ItemRegister.COMPACTED_SNOWBALL.get());
            if (itemStack.has(DataComponentRegister.REGION.get())) {
                stack.set(DataComponentRegister.REGION.get(), itemStack.get(DataComponentRegister.REGION.get()));
            }
            pPlayer.getInventory().placeItemBackInInventory(stack, true);
            if (!pPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, @NotNull Position position, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        SmoothSnowballEntity snowball = new SmoothSnowballEntity(level, position.x(), position.y(), position.z(), itemStack.get(DataComponentRegister.REGION));
        snowball.setItem(itemStack);
        return snowball;
    }

    @Override
    public void addMainTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.translatable("smooth_snowball.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
