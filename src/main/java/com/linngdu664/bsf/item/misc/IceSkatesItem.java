package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.registry.ArmorMaterialRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class IceSkatesItem extends Item {
    public IceSkatesItem() {
        super(com.linngdu664.bsf.Main.itemProperties()
                .stacksTo(1)
                .humanoidArmor(ArmorMaterialRegister.ICE_SKATES_ARMOR_MATERIAL, ArmorType.BOOTS)
                .durability(256)
                .repairable(Items.LEATHER_BOOTS));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("ice_skates.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

