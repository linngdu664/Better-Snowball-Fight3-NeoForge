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

public class SnowGolemCoreItem extends Item {
    private final int coolDown;
    private final String[] hoverText;
    private final ChatFormatting[] chatFormats;

    public SnowGolemCoreItem(int coolDown, String[] hoverText, ChatFormatting[] chatFormats) {
        super(new Properties().rarity(Rarity.UNCOMMON));
        this.coolDown = coolDown;
        this.hoverText = hoverText;
        this.chatFormats = chatFormats;
    }

    public int getCoolDown() {
        return coolDown;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        for (int i = 0, size = hoverText.length; i < size; i++) {
            tooltipComponents.add(MutableComponent.create(new TranslatableContents(hoverText[i], null, new Object[0])).withStyle(chatFormats[i]));
        }
    }
}
