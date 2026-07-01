package com.linngdu664.bsf.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class SnowballCannonUpgradeSmithingTemplateItem extends Item {
    public SnowballCannonUpgradeSmithingTemplateItem() {
        super(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template1.tooltip").withStyle(ChatFormatting.BLUE));
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template2.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("void.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template3.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template4.tooltip").withStyle(ChatFormatting.BLUE));
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template5.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("snowball_cannon_upgrade_smithing_template6.tooltip").withStyle(ChatFormatting.BLUE));
    }
}

