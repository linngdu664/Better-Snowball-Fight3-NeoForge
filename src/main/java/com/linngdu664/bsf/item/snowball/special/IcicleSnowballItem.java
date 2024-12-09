package com.linngdu664.bsf.item.snowball.special;

import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.special.IcicleSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IcicleSnowballItem extends AbstractSnowStorageSnowballItem {
    public IcicleSnowballItem() {
        super(Rarity.EPIC, new SnowballProperties().idForTank(31));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        return throwOrStorage(pPlayer, pLevel, pUsedHand, 1.7F, 15);
    }

    @Override
    public AbstractBSFSnowballEntity getCorrespondingEntity(Level level, LivingEntity livingEntity, ILaunchAdjustment launchAdjustment, RegionData region) {
        return new IcicleSnowballEntity(livingEntity, level, launchAdjustment, Math.min(absorbSnow(livingEntity, level), ServerConfig.ICICLE_SNOWBALL_CAPACITY.getConfigValue()), region);
    }

    @Override
    public void addMainTips(List<Component> pTooltipComponents) {
        super.addMainTips(pTooltipComponents);
        pTooltipComponents.add(Component.translatable("icicle_snowball.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
