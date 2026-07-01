package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.item.block.*;
import com.linngdu664.bsf.item.minigame_tool.RegionToolItem;
import com.linngdu664.bsf.item.minigame_tool.ScoringDeviceItem;
import com.linngdu664.bsf.item.minigame_tool.TeamLinkerItem;
import com.linngdu664.bsf.item.misc.*;
import com.linngdu664.bsf.item.snowball.CompactedSnowballSetItem;
import com.linngdu664.bsf.item.snowball.force.MonsterGravitySnowballItem;
import com.linngdu664.bsf.item.snowball.force.MonsterRepulsionSnowballItem;
import com.linngdu664.bsf.item.snowball.force.ProjectileGravitySnowballItem;
import com.linngdu664.bsf.item.snowball.force.ProjectileRepulsionSnowballItem;
import com.linngdu664.bsf.item.snowball.normal.*;
import com.linngdu664.bsf.item.snowball.special.*;
import com.linngdu664.bsf.item.snowball.tracking.*;
import com.linngdu664.bsf.item.tank.LargeSnowballTankItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.item.tool.*;
import com.linngdu664.bsf.item.weapon.*;
import com.linngdu664.bsf.misc.BSFTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ItemRegister {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MODID);

    private static <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier) {
        return ITEMS.register(name, id -> Main.withItemId(id, supplier));
    }

    public static final DeferredItem<Item> SMOOTH_SNOWBALL = register("smooth_snowball", SmoothSnowballItem::new);
    public static final DeferredItem<Item> DUCK_SNOWBALL = register("duck_snowball", DuckSnowballItem::new);
    public static final DeferredItem<Item> COMPACTED_SNOWBALL = register("compacted_snowball", CompactedSnowballItem::new);
    public static final DeferredItem<Item> COMPACTED_SNOWBALL_SET = register("compacted_snowball_set", CompactedSnowballSetItem::new);
    public static final DeferredItem<Item> STONE_SNOWBALL = register("stone_snowball", StoneSnowballItem::new);
    public static final DeferredItem<Item> GLASS_SNOWBALL = register("glass_snowball", GlassSnowballItem::new);
    public static final DeferredItem<Item> ICE_SNOWBALL = register("ice_snowball", IceSnowballItem::new);
    public static final DeferredItem<Item> IRON_SNOWBALL = register("iron_snowball", IronSnowballItem::new);
    public static final DeferredItem<Item> GOLD_SNOWBALL = register("gold_snowball", GoldSnowballItem::new);
    public static final DeferredItem<Item> OBSIDIAN_SNOWBALL = register("obsidian_snowball", ObsidianSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_SNOWBALL = register("explosive_snowball", ExplosiveSnowballItem::new);
    public static final DeferredItem<Item> SPECTRAL_SNOWBALL = register("spectral_snowball", SpectralSnowballItem::new);
    public static final DeferredItem<Item> FROZEN_SNOWBALL = register("frozen_snowball", FrozenSnowballItem::new);
    public static final DeferredItem<Item> POWDER_SNOWBALL = register("powder_snowball", PowderSnowballItem::new);
    public static final DeferredItem<Item> LIGHT_MONSTER_TRACKING_SNOWBALL = register("light_monster_tracking_snowball", LightMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> HEAVY_MONSTER_TRACKING_SNOWBALL = register("heavy_monster_tracking_snowball", HeavyMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_MONSTER_TRACKING_SNOWBALL = register("explosive_monster_tracking_snowball", ExplosiveMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> LIGHT_PLAYER_TRACKING_SNOWBALL = register("light_player_tracking_snowball", LightPlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> HEAVY_PLAYER_TRACKING_SNOWBALL = register("heavy_player_tracking_snowball", HeavyPlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_PLAYER_TRACKING_SNOWBALL = register("explosive_player_tracking_snowball", ExplosivePlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> MONSTER_GRAVITY_SNOWBALL = register("monster_gravity_snowball", MonsterGravitySnowballItem::new);
    public static final DeferredItem<Item> PROJECTILE_GRAVITY_SNOWBALL = register("projectile_gravity_snowball", ProjectileGravitySnowballItem::new);
    public static final DeferredItem<Item> MONSTER_REPULSION_SNOWBALL = register("monster_repulsion_snowball", MonsterRepulsionSnowballItem::new);
    public static final DeferredItem<Item> PROJECTILE_REPULSION_SNOWBALL = register("projectile_repulsion_snowball", ProjectileRepulsionSnowballItem::new);
    public static final DeferredItem<Item> BLACK_HOLE_SNOWBALL = register("black_hole_snowball", BlackHoleSnowballItem::new);
    public static final DeferredItem<Item> SUBSPACE_SNOWBALL = register("subspace_snowball", SubspaceSnowballItem::new);
    public static final DeferredItem<Item> THRUST_SNOWBALL = register("thrust_snowball", ThrustSnowballItem::new);
    public static final DeferredItem<Item> ENDER_SNOWBALL = register("ender_snowball", EnderSnowballItem::new);
    public static final DeferredItem<Item> EXPANSION_SNOWBALL = register("expansion_snowball", ExpansionSnowballItem::new);
    public static final DeferredItem<Item> RECONSTRUCT_SNOWBALL = register("reconstruct_snowball", ReconstructSnowballItem::new);
    public static final DeferredItem<Item> ICICLE_SNOWBALL = register("icicle_snowball", IcicleSnowballItem::new);
    public static final DeferredItem<Item> CRITICAL_FROZEN_SNOWBALL = register("critical_frozen_snowball", CriticalFrozenSnowballItem::new);
    public static final DeferredItem<Item> IMPULSE_SNOWBALL = register("impulse_snowball", ImpulseSnowballItem::new);
    public static final DeferredItem<Item> CHERRY_BLOSSOM_SNOWBALL = register("cherry_blossom_snowball", CherryBlossomSnowballItem::new);
    public static final DeferredItem<Item> GHOST_SNOWBALL = register("ghost_snowball", GhostSnowballItem::new);

    public static final DeferredItem<Item> SNOWBALL_TANK = register("snowball_tank", () -> new SnowballTankItem());
    public static final DeferredItem<Item> LARGE_SNOWBALL_TANK = register("large_snowball_tank", LargeSnowballTankItem::new);

    public static final DeferredItem<Item> WOOD_SNOWBALL_CLAMP = register("wood_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.WOOD, 118));
    public static final DeferredItem<Item> STONE_SNOWBALL_CLAMP = register("stone_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.STONE, 260));
    public static final DeferredItem<Item> IRON_SNOWBALL_CLAMP = register("iron_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.IRON, 500));
    public static final DeferredItem<Item> GOLD_SNOWBALL_CLAMP = register("gold_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.GOLD, 64));
    public static final DeferredItem<Item> DIAMOND_SNOWBALL_CLAMP = register("diamond_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.DIAMOND, 3122));
    public static final DeferredItem<Item> NETHERITE_SNOWBALL_CLAMP = register("netherite_snowball_clamp", () -> new SnowballClampItem(ToolMaterial.NETHERITE, 4062));
    public static final DeferredItem<Item> EMERALD_SNOWBALL_CLAMP = register("emerald_snowball_clamp", () -> new SnowballClampItem(BSFTiers.EMERALD, 2866));
    public static final DeferredItem<Item> SNOWBALL_CANNON = register("snowball_cannon", () -> new SnowballCannonItem());
    public static final DeferredItem<Item> POWERFUL_SNOWBALL_CANNON = register("powerful_snowball_cannon", PowerfulSnowballCannonItem::new);
    public static final DeferredItem<Item> FREEZING_SNOWBALL_CANNON = register("freezing_snowball_cannon", FreezingSnowballCannonItem::new);
    public static final DeferredItem<Item> IMPLOSION_SNOWBALL_CANNON = register("implosion_snowball_cannon", ImplosionSnowballCannonItem::new);
    public static final DeferredItem<Item> SNOWBALL_MACHINE_GUN = register("snowball_machine_gun", SnowballMachineGunItem::new);
    public static final DeferredItem<Item> SNOWBALL_SHOTGUN = register("snowball_shotgun", SnowballShotgunItem::new);
    public static final DeferredItem<Item> GLOVE = register("glove", GloveItem::new);
    public static final DeferredItem<Item> JEDI_GLOVE = register("jedi_glove", JediGloveItem::new);
    public static final DeferredItem<Item> REPULSIVE_FIELD_GENERATOR = register("repulsive_field_generator", RepulsiveFieldGeneratorItem::new);


    public static final DeferredItem<Item> ICE_SKATES_ITEM = register("ice_skates", IceSkatesItem::new);
    public static final DeferredItem<Item> SNOW_FALL_BOOTS = register("snow_fall_boots", SnowFallBootsItem::new);
    public static final DeferredItem<Item> SNOW_BLOCK_BLENDER = register("snow_block_blender", SnowBlockBlenderItem::new);
    public static final DeferredItem<Item> BASIN = register("basin", BasinItem::new);
    public static final DeferredItem<Item> SNOW_GOLEM_MODE_TWEAKER = register("snow_golem_mode_tweaker", SnowGolemModeTweakerItem::new);
    public static final DeferredItem<Item> CREATIVE_SNOW_GOLEM_TOOL = register("creative_snow_golem_tool", CreativeSnowGolemToolItem::new);
    public static final DeferredItem<Item> TARGET_LOCATOR = register("target_locator", TargetLocatorItem::new);
    public static final DeferredItem<Item> SNOW_TRAP_SETTER = register("snow_trap_setter", SnowTrapSetterItem::new);
    public static final DeferredItem<Item> SCULK_SNOWBALL_LAUNCHER = register("sculk_snowball_launcher", SculkSnowballLauncherItem::new);
    public static final DeferredItem<Item> SNOW_GOLEM_CONTAINER = register("snow_golem_container", SnowGolemContainer::new);
    public static final DeferredItem<Item> VECTOR_INVERSION_ANCHOR = register("vector_inversion_anchor", VectorInversionAnchorItem::new);
    public static final DeferredItem<Item> COLD_COMPRESSION_JET_ENGINE = register("cold_compression_jet_engine", ColdCompressionJetEngineItem::new);


    public static final DeferredItem<Item> POPSICLE = register("popsicle", PopsicleItem::new);
    public static final DeferredItem<Item> MILK_POPSICLE = register("milk_popsicle", MilkPopsicleItem::new);
    public static final DeferredItem<Item> VODKA = register("vodka", VodkaItem::new);
    public static final DeferredItem<Item> SUSPICIOUS_USB_FLASH_DRIVE = register("suspicious_usb_flash_drive", SuspiciousUSBFlashDriveItem::new);


    public static final DeferredItem<Item> SMART_SNOW_BLOCK = register("smart_snow_block", SmartSnowBlockItem::new);


    public static final DeferredItem<Item> SUPER_POWER_CORE = register("super_power_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SUPER_FROZEN_CORE = register("super_frozen_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> TRACKING_CORE = register("tracking_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> UNSTABLE_CORE = register("unstable_core", UnstableCoreItem::new);
    public static final DeferredItem<Item> REPULSION_CORE = register("repulsion_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> GRAVITY_CORE = register("gravity_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SNOWBALL_CANNON_UPGRADE_SMITHING_TEMPLATE = register("snowball_cannon_upgrade_smithing_template", SnowballCannonUpgradeSmithingTemplateItem::new);
    public static final DeferredItem<Item> SNOWMAN_IN_HAND = register("snowman_in_hand", SnowmanInHandItem::new);

    public static final DeferredItem<Item> THRUST_GOLEM_CORE = register("thrust_golem_core", () -> new SnowGolemCoreItem(120, new String[]{"thrust_golem_core.tooltip","thrust_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> SWIFTNESS_GOLEM_CORE = register("swiftness_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"swiftness_golem_core.tooltip","swiftness_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> REGENERATION_GOLEM_CORE = register("regeneration_golem_core", () -> new SnowGolemCoreItem(100, new String[]{"regeneration_golem_core.tooltip","regeneration_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> CRITICAL_SNOW_GOLEM_CORE = register("critical_snow_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"critical_snow_golem_core.tooltip","critical_snow_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> REPULSIVE_FIELD_GOLEM_CORE = register("repulsive_field_golem_core", () -> new SnowGolemCoreItem(100, new String[]{"repulsive_field_golem_core.tooltip","repulsive_field_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> NEAR_TELEPORTATION_GOLEM_CORE = register("near_teleportation_golem_core", () -> new SnowGolemCoreItem(160, new String[]{"near_teleportation_golem_core.tooltip","near_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> ENDER_TELEPORTATION_GOLEM_CORE = register("ender_teleportation_golem_core", () -> new SnowGolemCoreItem(200, new String[]{"ender_teleportation_golem_core.tooltip","ender_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> ACTIVE_TELEPORTATION_GOLEM_CORE = register("active_teleportation_golem_core", () -> new SnowGolemCoreItem(240, new String[]{"active_teleportation_golem_core.tooltip","active_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> MOVEMENT_PREDICTION_GOLEM_CORE = register("movement_prediction_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"movement_prediction_golem_core.tooltip","movement_prediction_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));

    public static final DeferredItem<Item> SNOW_GOLEM_CORE_REMOVER = register("snow_golem_core_remover", () -> new Item(com.linngdu664.bsf.Main.itemProperties().stacksTo(1)));
    public static final DeferredItem<Item> BLANK_GOLEM_CORE = register("blank_golem_core", () -> new Item(com.linngdu664.bsf.Main.itemProperties()));



    public static final DeferredItem<Item> GPS_SNOWBALL = register("gps_snowball", () -> new Item(com.linngdu664.bsf.Main.itemProperties()));    //This item does not need to be added to the group
    public static final DeferredItem<Item> SCULK_SNOWBALL = register("sculk_snowball", () -> new Item(com.linngdu664.bsf.Main.itemProperties()));


    public static final DeferredItem<Item> WHITE_TEAM_LINKER = register("white_team_linker", () -> new TeamLinkerItem(0));
    public static final DeferredItem<Item> ORANGE_TEAM_LINKER = register("orange_team_linker", () -> new TeamLinkerItem(1));
    public static final DeferredItem<Item> MAGENTA_TEAM_LINKER = register("magenta_team_linker", () -> new TeamLinkerItem(2));
    public static final DeferredItem<Item> LIGHT_BLUE_TEAM_LINKER = register("light_blue_team_linker", () -> new TeamLinkerItem(3));
    public static final DeferredItem<Item> YELLOW_TEAM_LINKER = register("yellow_team_linker", () -> new TeamLinkerItem(4));
    public static final DeferredItem<Item> LIME_TEAM_LINKER = register("lime_team_linker", () -> new TeamLinkerItem(5));
    public static final DeferredItem<Item> PINK_TEAM_LINKER = register("pink_team_linker", () -> new TeamLinkerItem(6));
    public static final DeferredItem<Item> GRAY_TEAM_LINKER = register("gray_team_linker", () -> new TeamLinkerItem(7));
    public static final DeferredItem<Item> LIGHT_GRAY_TEAM_LINKER = register("light_gray_team_linker", () -> new TeamLinkerItem(8));
    public static final DeferredItem<Item> CYAN_TEAM_LINKER = register("cyan_team_linker", () -> new TeamLinkerItem(9));
    public static final DeferredItem<Item> PURPLE_TEAM_LINKER = register("purple_team_linker", () -> new TeamLinkerItem(10));
    public static final DeferredItem<Item> BLUE_TEAM_LINKER = register("blue_team_linker", () -> new TeamLinkerItem(11));
    public static final DeferredItem<Item> BROWN_TEAM_LINKER = register("brown_team_linker", () -> new TeamLinkerItem(12));
    public static final DeferredItem<Item> GREEN_TEAM_LINKER = register("green_team_linker", () -> new TeamLinkerItem(13));
    public static final DeferredItem<Item> RED_TEAM_LINKER = register("red_team_linker", () -> new TeamLinkerItem(14));
    public static final DeferredItem<Item> BLACK_TEAM_LINKER = register("black_team_linker", () -> new TeamLinkerItem(15));

    public static final DeferredItem<Item> REGION_TOOL = register("region_tool", RegionToolItem::new);
    public static final DeferredItem<Item> SCORING_DEVICE = register("scoring_device", ScoringDeviceItem::new);
    public static final DeferredItem<Item> VENDING_MACHINE = register("vending_machine", VendingMachineItem::new);
    public static final DeferredItem<Item> REGION_CONTROLLER = register("region_controller", RegionControllerItem::new);
    public static final DeferredItem<Item> REGION_CONTROLLER_VIEW = register("region_controller_view", RegionControllerViewItem::new);
    public static final DeferredItem<Item> REGION_PLAYER_INSPECTOR = register("region_player_inspector", RegionPlayerInspectorItem::new);
}


