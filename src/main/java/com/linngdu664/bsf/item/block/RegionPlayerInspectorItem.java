package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;

public class RegionPlayerInspectorItem extends BlockItem {
    public RegionPlayerInspectorItem() {
        super(BlockRegister.REGION_PLAYER_INSPECTOR.get(), new Properties().rarity(Rarity.UNCOMMON));
    }
}
