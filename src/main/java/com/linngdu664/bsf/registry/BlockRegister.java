package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Main.MODID);

    public static final DeferredHolder<Block, SmartSnowBlock> SMART_SNOW_BLOCK = BLOCKS.register("smart_snow_block", () -> new SmartSnowBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.SNOW)));
    public static final DeferredHolder<Block, LooseSnowBlock> LOOSE_SNOW_BLOCK = BLOCKS.register("loose_snow_block", LooseSnowBlock::new);
    public static final DeferredHolder<Block, SnowTrap> SNOW_TRAP = BLOCKS.register("snow_trap", SnowTrap::new);
    public static final DeferredHolder<Block, CriticalSnow> CRITICAL_SNOW = BLOCKS.register("critical_snow", CriticalSnow::new);
    public static final DeferredHolder<Block, VendingMachineBlock> VENDING_MACHINE_BLOCK = BLOCKS.register("vending_machine", VendingMachineBlock::new);
    public static final DeferredHolder<Block, RegionControllerBlock> REGION_CONTROLLER_BLOCK = BLOCKS.register("region_controller", RegionControllerBlock::new);
}
