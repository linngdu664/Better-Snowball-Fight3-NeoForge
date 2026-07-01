package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CriticalSnowEntity>> CRITICAL_SNOW = BLOCK_ENTITIES.register("critical_snow", () -> new BlockEntityType<>(CriticalSnowEntity::new, BlockRegister.CRITICAL_SNOW.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegionControllerBlockEntity>> REGION_CONTROLLER = BLOCK_ENTITIES.register("region_controller", () -> new BlockEntityType<>(RegionControllerBlockEntity::new, BlockRegister.REGION_CONTROLLER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegionControllerViewBlockEntity>> REGION_CONTROLLER_VIEW = BLOCK_ENTITIES.register("region_controller_view", () -> new BlockEntityType<>(RegionControllerViewBlockEntity::new, BlockRegister.REGION_CONTROLLER_VIEW.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VendingMachineBlockEntity>> VENDING_MACHINE = BLOCK_ENTITIES.register("vending_machine", () -> new BlockEntityType<>(VendingMachineBlockEntity::new, BlockRegister.VENDING_MACHINE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RegionPlayerInspectorBlockEntity>> REGION_PLAYER_INSPECTOR = BLOCK_ENTITIES.register("region_player_inspector", () -> new BlockEntityType<>(RegionPlayerInspectorBlockEntity::new, BlockRegister.REGION_PLAYER_INSPECTOR.get()));
}
