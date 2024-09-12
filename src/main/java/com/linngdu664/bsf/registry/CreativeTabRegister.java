package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.item.component.ItemData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegister {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    // Creates a creative tab with the id "bsf:bsf_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BSF_TAB = CREATIVE_TABS.register("bsf_tab", () -> net.minecraft.world.item.CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ItemRegister.EXPLOSIVE_SNOWBALL.get().getDefaultInstance())
            .title(MutableComponent.create(new TranslatableContents("itemGroup.bsf_group", null, new Object[0])))
            .displayItems((parameters, output) -> {
                output.accept(ItemRegister.SMOOTH_SNOWBALL.get());
                output.accept(ItemRegister.DUCK_SNOWBALL.get());
                output.accept(ItemRegister.COMPACTED_SNOWBALL.get());
                output.accept(ItemRegister.COMPACTED_SNOWBALL_SET.get());
                output.accept(ItemRegister.CHERRY_BLOSSOM_SNOWBALL.get());
                output.accept(ItemRegister.STONE_SNOWBALL.get());
                output.accept(ItemRegister.GLASS_SNOWBALL.get());
                output.accept(ItemRegister.ICE_SNOWBALL.get());
                output.accept(ItemRegister.IRON_SNOWBALL.get());
                output.accept(ItemRegister.GOLD_SNOWBALL.get());
                output.accept(ItemRegister.OBSIDIAN_SNOWBALL.get());
                output.accept(ItemRegister.EXPLOSIVE_SNOWBALL.get());
                output.accept(ItemRegister.THRUST_SNOWBALL.get());

                output.accept(ItemRegister.LIGHT_MONSTER_TRACKING_SNOWBALL.get());
                output.accept(ItemRegister.HEAVY_MONSTER_TRACKING_SNOWBALL.get());
                output.accept(ItemRegister.EXPLOSIVE_MONSTER_TRACKING_SNOWBALL.get());
                output.accept(ItemRegister.LIGHT_PLAYER_TRACKING_SNOWBALL.get());
                output.accept(ItemRegister.HEAVY_PLAYER_TRACKING_SNOWBALL.get());
                output.accept(ItemRegister.EXPLOSIVE_PLAYER_TRACKING_SNOWBALL.get());

                output.accept(ItemRegister.MONSTER_GRAVITY_SNOWBALL.get());
                output.accept(ItemRegister.PROJECTILE_GRAVITY_SNOWBALL.get());
                output.accept(ItemRegister.MONSTER_REPULSION_SNOWBALL.get());
                output.accept(ItemRegister.PROJECTILE_REPULSION_SNOWBALL.get());
                output.accept(ItemRegister.IMPULSE_SNOWBALL.get());
                output.accept(ItemRegister.BLACK_HOLE_SNOWBALL.get());
                output.accept(ItemRegister.SUBSPACE_SNOWBALL.get());

                output.accept(ItemRegister.POWDER_SNOWBALL.get());
                output.accept(ItemRegister.SPECTRAL_SNOWBALL.get());
                output.accept(ItemRegister.FROZEN_SNOWBALL.get());
                output.accept(ItemRegister.CRITICAL_FROZEN_SNOWBALL.get());
                output.accept(ItemRegister.ENDER_SNOWBALL.get());
                output.accept(ItemRegister.GHOST_SNOWBALL.get());

                output.accept(ItemRegister.EXPANSION_SNOWBALL.get());
                output.accept(ItemRegister.RECONSTRUCT_SNOWBALL.get());
                output.accept(ItemRegister.ICICLE_SNOWBALL.get());



                Item tank = ItemRegister.SNOWBALL_TANK.get();
                ItemStack itemStack = tank.getDefaultInstance();
                itemStack.setDamageValue(itemStack.getMaxDamage());
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.COMPACTED_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.CHERRY_BLOSSOM_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.STONE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GLASS_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ICE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.IRON_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GOLD_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.OBSIDIAN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.THRUST_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.LIGHT_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.HEAVY_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.LIGHT_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.HEAVY_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.MONSTER_GRAVITY_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.MONSTER_REPULSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.PROJECTILE_GRAVITY_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.PROJECTILE_REPULSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.IMPULSE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.BLACK_HOLE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.SUBSPACE_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.POWDER_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.SPECTRAL_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.FROZEN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.CRITICAL_FROZEN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ENDER_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GHOST_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPANSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.RECONSTRUCT_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ICICLE_SNOWBALL.get()));
                output.accept(itemStack);


                tank = ItemRegister.LARGE_SNOWBALL_TANK.get();
                itemStack = tank.getDefaultInstance();
                itemStack.setDamageValue(itemStack.getMaxDamage());
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.COMPACTED_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.CHERRY_BLOSSOM_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.STONE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GLASS_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ICE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.IRON_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GOLD_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.OBSIDIAN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.THRUST_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.LIGHT_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.HEAVY_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_MONSTER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.LIGHT_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.HEAVY_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPLOSIVE_PLAYER_TRACKING_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.MONSTER_GRAVITY_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.MONSTER_REPULSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.PROJECTILE_GRAVITY_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.PROJECTILE_REPULSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.IMPULSE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.BLACK_HOLE_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.SUBSPACE_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.POWDER_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.SPECTRAL_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.FROZEN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.CRITICAL_FROZEN_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ENDER_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.GHOST_SNOWBALL.get()));
                output.accept(itemStack);

                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.EXPANSION_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.RECONSTRUCT_SNOWBALL.get()));
                output.accept(itemStack);
                itemStack = tank.getDefaultInstance();
                itemStack.set(DataComponentRegister.AMMO_ITEM, new ItemData(ItemRegister.ICICLE_SNOWBALL.get()));
                output.accept(itemStack);

                output.accept(ItemRegister.WOOD_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.STONE_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.IRON_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.GOLD_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.DIAMOND_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.NETHERITE_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.EMERALD_SNOWBALL_CLAMP.get());
                output.accept(ItemRegister.BASIN.get());
                itemStack = ItemRegister.BASIN.get().getDefaultInstance();
                itemStack.set(DataComponentRegister.BASIN_SNOW_TYPE, (byte) 1);
                output.accept(itemStack);
                itemStack = ItemRegister.BASIN.get().getDefaultInstance();
                itemStack.set(DataComponentRegister.BASIN_SNOW_TYPE, (byte) 2);
                output.accept(itemStack);
                output.accept(ItemRegister.SNOWBALL_CANNON.get());
                output.accept(ItemRegister.POWERFUL_SNOWBALL_CANNON.get());
                output.accept(ItemRegister.FREEZING_SNOWBALL_CANNON.get());
                output.accept(ItemRegister.IMPLOSION_SNOWBALL_CANNON.get());
                output.accept(ItemRegister.SNOWBALL_MACHINE_GUN.get());
                output.accept(ItemRegister.SNOWBALL_SHOTGUN.get());
                output.accept(ItemRegister.SNOWMAN_IN_HAND.get());
                output.accept(ItemRegister.TARGET_LOCATOR.get());
                output.accept(ItemRegister.SCULK_SNOWBALL_LAUNCHER.get());

                output.accept(ItemRegister.SNOW_BLOCK_BLENDER.get());
                output.accept(ItemRegister.SNOW_TRAP_SETTER.get());
                output.accept(ItemRegister.REPULSIVE_FIELD_GENERATOR.get());
                output.accept(ItemRegister.GLOVE.get());
                output.accept(ItemRegister.JEDI_GLOVE.get());
                output.accept(ItemRegister.VECTOR_INVERSION_ANCHOR.get());
                output.accept(ItemRegister.COLD_COMPRESSION_JET_ENGINE.get());

                output.accept(ItemRegister.ICE_SKATES_ITEM.get());
                output.accept(ItemRegister.SNOW_FALL_BOOTS.get());

                output.accept(ItemRegister.POPSICLE.get());
                output.accept(ItemRegister.MILK_POPSICLE.get());
                output.accept(ItemRegister.VODKA.get());

                output.accept(ItemRegister.SMART_SNOW_BLOCK.get());
                output.accept(ItemRegister.SNOW_GOLEM_CONTAINER.get());
                output.accept(ItemRegister.SNOW_GOLEM_MODE_TWEAKER.get());
                output.accept(ItemRegister.CREATIVE_SNOW_GOLEM_TOOL.get());
                output.accept(ItemRegister.SNOW_GOLEM_CORE_REMOVER.get());
                output.accept(ItemRegister.BLANK_GOLEM_CORE.get());
                output.accept(ItemRegister.THRUST_GOLEM_CORE.get());
                output.accept(ItemRegister.SWIFTNESS_GOLEM_CORE.get());
                output.accept(ItemRegister.REGENERATION_GOLEM_CORE.get());
                output.accept(ItemRegister.CRITICAL_SNOW_GOLEM_CORE.get());
                output.accept(ItemRegister.REPULSIVE_FIELD_GOLEM_CORE.get());
                output.accept(ItemRegister.NEAR_TELEPORTATION_GOLEM_CORE.get());
                output.accept(ItemRegister.ENDER_TELEPORTATION_GOLEM_CORE.get());
                output.accept(ItemRegister.ACTIVE_TELEPORTATION_GOLEM_CORE.get());
                output.accept(ItemRegister.MOVEMENT_PREDICTION_GOLEM_CORE.get());

                output.accept(ItemRegister.SNOWBALL_CANNON_UPGRADE_SMITHING_TEMPLATE.get());
                output.accept(ItemRegister.SUPER_POWER_CORE.get());
                output.accept(ItemRegister.SUPER_FROZEN_CORE.get());

                output.accept(ItemRegister.TRACKING_CORE.get());
                output.accept(ItemRegister.REPULSION_CORE.get());
                output.accept(ItemRegister.GRAVITY_CORE.get());
                output.accept(ItemRegister.UNSTABLE_CORE.get());

                output.accept(ItemRegister.WHITE_TEAM_LINKER.get());
                output.accept(ItemRegister.ORANGE_TEAM_LINKER.get());
                output.accept(ItemRegister.MAGENTA_TEAM_LINKER.get());
                output.accept(ItemRegister.LIGHT_BLUE_TEAM_LINKER.get());
                output.accept(ItemRegister.YELLOW_TEAM_LINKER.get());
                output.accept(ItemRegister.LIME_TEAM_LINKER.get());
                output.accept(ItemRegister.PINK_TEAM_LINKER.get());
                output.accept(ItemRegister.GRAY_TEAM_LINKER.get());
                output.accept(ItemRegister.LIGHT_GRAY_TEAM_LINKER.get());
                output.accept(ItemRegister.CYAN_TEAM_LINKER.get());
                output.accept(ItemRegister.PURPLE_TEAM_LINKER.get());
                output.accept(ItemRegister.BLUE_TEAM_LINKER.get());
                output.accept(ItemRegister.BROWN_TEAM_LINKER.get());
                output.accept(ItemRegister.GREEN_TEAM_LINKER.get());
                output.accept(ItemRegister.RED_TEAM_LINKER.get());
                output.accept(ItemRegister.BLACK_TEAM_LINKER.get());

                output.accept(ItemRegister.ZONE_CONTROLLER.get());
                output.accept(ItemRegister.VENDING_MACHINE.get());
                output.accept(ItemRegister.SCORING_DEVICE.get());
                output.accept(ItemRegister.REGION_TOOL.get());
                output.accept(ItemRegister.VALUE_ADJUSTMENT_TOOL.get());
            }).build());
}
