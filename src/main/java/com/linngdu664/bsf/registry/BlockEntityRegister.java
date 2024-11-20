package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.CriticalSnowEntity;
import com.linngdu664.bsf.block.entity.RegionControllerBlockEntity;
import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CriticalSnowEntity>> CRITICAL_SNOW_ENTITY = BLOCK_ENTITIES.register("critical_snow", () -> BlockEntityType.Builder.of(CriticalSnowEntity::new, BlockRegister.CRITICAL_SNOW.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegionControllerBlockEntity>> REGION_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITIES.register("region_controller", () -> BlockEntityType.Builder.of(RegionControllerBlockEntity::new, BlockRegister.REGION_CONTROLLER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VendingMachineBlockEntity>> VENDING_MACHINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("vending_machine", () -> BlockEntityType.Builder.of(VendingMachineBlockEntity::new, BlockRegister.VENDING_MACHINE_BLOCK.get()).build(null));
}
