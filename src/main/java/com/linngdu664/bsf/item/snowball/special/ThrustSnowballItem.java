package com.linngdu664.bsf.item.snowball.special;

import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.weapon.ImplosionSnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ThrustSnowballItem extends AbstractBSFSnowballItem {
    public ThrustSnowballItem() {
        super(Rarity.COMMON, new SnowballProperties()
                .idForTank(14)
                .allowLaunchTypeFlag(SnowballShotgunItem.TYPE_FLAG | ImplosionSnowballCannonItem.TYPE_FLAG)
                .shotgunPushRank(0.38)
        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        storageInTank(pPlayer);
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    @Override
    public void addUsageTips(List<Component> pTooltipComponents) {

    }

    @Override
    public void addMainTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.translatable("thrust_snowball.tooltip").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("thrust_snowball1.tooltip").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("thrust_snowball2.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
