package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;

public class ZoneControllerItem extends BlockItem {
    public ZoneControllerItem() {
        super(BlockRegister.REGION_CONTROLLER.get(), new Properties().rarity(Rarity.UNCOMMON));
    }


}
