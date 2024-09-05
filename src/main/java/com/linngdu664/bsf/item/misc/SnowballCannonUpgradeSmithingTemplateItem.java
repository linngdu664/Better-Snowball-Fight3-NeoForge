package com.linngdu664.bsf.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
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
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template1.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template2.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("void.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template3.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template4.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template5.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_cannon_upgrade_smithing_template6.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
    }
}
