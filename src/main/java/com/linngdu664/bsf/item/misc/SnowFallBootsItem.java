package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.registry.ArmorMaterialRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class SnowFallBootsItem extends Item {
    public SnowFallBootsItem() {
        super(com.linngdu664.bsf.Main.itemProperties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1)
                .humanoidArmor(ArmorMaterialRegister.SNOW_FALL_BOOTS_ARMOR_MATERIAL, ArmorType.BOOTS)
                .durability(810)
                .repairable(Items.LEATHER_BOOTS));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("snow_fall_boots.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("snow_fall_boots1.tooltip", Component.translatable("enchantment.bsf.kinetic_energy_storage")).withStyle(ChatFormatting.GRAY));
    }
}

