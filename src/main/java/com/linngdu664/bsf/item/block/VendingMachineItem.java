package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;

public class VendingMachineItem extends BlockItem {
    public VendingMachineItem() {
        super(BlockRegister.VENDING_MACHINE.get(), new Properties().rarity(Rarity.UNCOMMON));
    }
}
