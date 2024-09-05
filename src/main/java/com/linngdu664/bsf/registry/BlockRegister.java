package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.CriticalSnow;
import com.linngdu664.bsf.block.LooseSnowBlock;
import com.linngdu664.bsf.block.SmartSnowBlock;
import com.linngdu664.bsf.block.SnowTrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Main.MODID);

    public static final DeferredHolder<Block, SmartSnowBlock> SMART_SNOW_BLOCK = BLOCKS.register("smart_snow_block", SmartSnowBlock::new);
    public static final DeferredHolder<Block, LooseSnowBlock> LOOSE_SNOW_BLOCK = BLOCKS.register("loose_snow_block", LooseSnowBlock::new);
    public static final DeferredHolder<Block, SnowTrap> SNOW_TRAP = BLOCKS.register("snow_trap", SnowTrap::new);
    public static final DeferredHolder<Block, CriticalSnow> CRITICAL_SNOW = BLOCKS.register("critical_snow", CriticalSnow::new);
}
