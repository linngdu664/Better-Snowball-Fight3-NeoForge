package com.linngdu664.bsf.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VendingMachine extends Block {
    public VendingMachine() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }
}
