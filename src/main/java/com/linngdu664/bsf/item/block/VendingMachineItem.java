package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VendingMachineItem extends BlockItem {
    public VendingMachineItem() {
        super(BlockRegister.VENDING_MACHINE.get(), new Properties().rarity(Rarity.UNCOMMON));
    }


}
