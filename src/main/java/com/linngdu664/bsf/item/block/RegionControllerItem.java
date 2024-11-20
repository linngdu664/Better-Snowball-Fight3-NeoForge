package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;

public class RegionControllerItem extends BlockItem {
    public RegionControllerItem() {
        super(BlockRegister.REGION_CONTROLLER_BLOCK.get(), new Properties().rarity(Rarity.UNCOMMON));
    }
}
