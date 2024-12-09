package com.linngdu664.bsf.item.snowball.force;

import com.linngdu664.bsf.entity.snowball.force.MonsterGravitySnowballEntity;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MonsterGravitySnowballItem extends AbstractBSFSnowballItem {
    public MonsterGravitySnowballItem() {
        super(Rarity.RARE, new SnowballProperties().idForTank(21).allowLaunchTypeFlag(HAND_TYPE_FLAG));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isShiftKeyDown()) {
            ItemStack newStack = new ItemStack(ItemRegister.PROJECTILE_GRAVITY_SNOWBALL.get(), itemStack.getCount());
            if (itemStack.has(DataComponentRegister.REGION.get())) {
                newStack.set(DataComponentRegister.REGION.get(), itemStack.get(DataComponentRegister.REGION.get()));
            }
            pPlayer.setItemInHand(pUsedHand, newStack);
        } else if (!storageInTank(pPlayer)) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                MonsterGravitySnowballEntity snowballEntity = new MonsterGravitySnowballEntity(pPlayer, pLevel, getLaunchAdjustment(1), itemStack.get(DataComponentRegister.REGION.get()));
                snowballEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F * getSnowballSlowdownRate(pPlayer), 1.0F);
                pLevel.addFreshEntity(snowballEntity);
            }
            if (!pPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
                pPlayer.getCooldowns().addCooldown(this, 40);
            }
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));//Feedback effect
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    @Override
    public void addUsageTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.translatable("can_change.tooltip", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void addMainTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.translatable("monster_gravity_snowball.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
