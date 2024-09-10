package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.world.item.Item;

public class ValueAdjustmentTool extends Item {
    public ValueAdjustmentTool(Properties properties) {
        super(properties.component(DataComponentRegister.GENERIC_INT_VALUE.get(), 0));
    }
}
