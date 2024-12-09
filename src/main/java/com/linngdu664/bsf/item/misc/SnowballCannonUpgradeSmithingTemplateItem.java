package com.linngdu664.bsf.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SnowballCannonUpgradeSmithingTemplateItem extends Item {
    public SnowballCannonUpgradeSmithingTemplateItem() {
        super(new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template1.tooltip").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template2.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("void.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template3.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template4.tooltip").withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template5.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("snowball_cannon_upgrade_smithing_template6.tooltip").withStyle(ChatFormatting.BLUE));
    }
}
