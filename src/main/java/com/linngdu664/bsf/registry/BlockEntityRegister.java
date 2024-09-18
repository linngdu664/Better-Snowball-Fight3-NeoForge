package com.linngdu664.bsf.registry;


import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.CriticalSnowEntity;
import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.block.entity.RegionControllerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CriticalSnowEntity>> CRITICAL_SNOW_ENTITY = BLOCK_ENTITIES.register("critical_snow_entity", () -> BlockEntityType.Builder.of(CriticalSnowEntity::new, BlockRegister.CRITICAL_SNOW.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegionControllerEntity>> REGION_CONTROLLER_ENTITY = BLOCK_ENTITIES.register("region_controller_entity", () -> BlockEntityType.Builder.of(RegionControllerEntity::new, BlockRegister.REGION_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VendingMachineEntity>> VENDING_MACHINE_ENTITY = BLOCK_ENTITIES.register("vending_machine_entity", () -> BlockEntityType.Builder.of(VendingMachineEntity::new, BlockRegister.VENDING_MACHINE.get()).build(null));
}
