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
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class ItemRegister {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MODID);

    public static final DeferredItem<Item> SMOOTH_SNOWBALL = ITEMS.register("smooth_snowball", SmoothSnowballItem::new);
    public static final DeferredItem<Item> DUCK_SNOWBALL = ITEMS.register("duck_snowball", DuckSnowballItem::new);
    public static final DeferredItem<Item> COMPACTED_SNOWBALL = ITEMS.register("compacted_snowball", CompactedSnowballItem::new);
    public static final DeferredItem<Item> COMPACTED_SNOWBALL_SET = ITEMS.register("compacted_snowball_set", CompactedSnowballSetItem::new);
    public static final DeferredItem<Item> STONE_SNOWBALL = ITEMS.register("stone_snowball", StoneSnowballItem::new);
    public static final DeferredItem<Item> GLASS_SNOWBALL = ITEMS.register("glass_snowball", GlassSnowballItem::new);
    public static final DeferredItem<Item> ICE_SNOWBALL = ITEMS.register("ice_snowball", IceSnowballItem::new);
    public static final DeferredItem<Item> IRON_SNOWBALL = ITEMS.register("iron_snowball", IronSnowballItem::new);
    public static final DeferredItem<Item> GOLD_SNOWBALL = ITEMS.register("gold_snowball", GoldSnowballItem::new);
    public static final DeferredItem<Item> OBSIDIAN_SNOWBALL = ITEMS.register("obsidian_snowball", ObsidianSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_SNOWBALL = ITEMS.register("explosive_snowball", ExplosiveSnowballItem::new);
    public static final DeferredItem<Item> SPECTRAL_SNOWBALL = ITEMS.register("spectral_snowball", SpectralSnowballItem::new);
    public static final DeferredItem<Item> FROZEN_SNOWBALL = ITEMS.register("frozen_snowball", FrozenSnowballItem::new);
    public static final DeferredItem<Item> POWDER_SNOWBALL = ITEMS.register("powder_snowball", PowderSnowballItem::new);
    public static final DeferredItem<Item> LIGHT_MONSTER_TRACKING_SNOWBALL = ITEMS.register("light_monster_tracking_snowball", LightMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> HEAVY_MONSTER_TRACKING_SNOWBALL = ITEMS.register("heavy_monster_tracking_snowball", HeavyMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_MONSTER_TRACKING_SNOWBALL = ITEMS.register("explosive_monster_tracking_snowball", ExplosiveMonsterTrackingSnowballItem::new);
    public static final DeferredItem<Item> LIGHT_PLAYER_TRACKING_SNOWBALL = ITEMS.register("light_player_tracking_snowball", LightPlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> HEAVY_PLAYER_TRACKING_SNOWBALL = ITEMS.register("heavy_player_tracking_snowball", HeavyPlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> EXPLOSIVE_PLAYER_TRACKING_SNOWBALL = ITEMS.register("explosive_player_tracking_snowball", ExplosivePlayerTrackingSnowballItem::new);
    public static final DeferredItem<Item> MONSTER_GRAVITY_SNOWBALL = ITEMS.register("monster_gravity_snowball", MonsterGravitySnowballItem::new);
    public static final DeferredItem<Item> PROJECTILE_GRAVITY_SNOWBALL = ITEMS.register("projectile_gravity_snowball", ProjectileGravitySnowballItem::new);
    public static final DeferredItem<Item> MONSTER_REPULSION_SNOWBALL = ITEMS.register("monster_repulsion_snowball", MonsterRepulsionSnowballItem::new);
    public static final DeferredItem<Item> PROJECTILE_REPULSION_SNOWBALL = ITEMS.register("projectile_repulsion_snowball", ProjectileRepulsionSnowballItem::new);
    public static final DeferredItem<Item> BLACK_HOLE_SNOWBALL = ITEMS.register("black_hole_snowball", BlackHoleSnowballItem::new);
    public static final DeferredItem<Item> SUBSPACE_SNOWBALL = ITEMS.register("subspace_snowball", SubspaceSnowballItem::new);
    public static final DeferredItem<Item> THRUST_SNOWBALL = ITEMS.register("thrust_snowball", ThrustSnowballItem::new);
    public static final DeferredItem<Item> ENDER_SNOWBALL = ITEMS.register("ender_snowball", EnderSnowballItem::new);
    public static final DeferredItem<Item> EXPANSION_SNOWBALL = ITEMS.register("expansion_snowball", ExpansionSnowballItem::new);
    public static final DeferredItem<Item> RECONSTRUCT_SNOWBALL = ITEMS.register("reconstruct_snowball", ReconstructSnowballItem::new);
    public static final DeferredItem<Item> ICICLE_SNOWBALL = ITEMS.register("icicle_snowball", IcicleSnowballItem::new);
    public static final DeferredItem<Item> CRITICAL_FROZEN_SNOWBALL = ITEMS.register("critical_frozen_snowball", CriticalFrozenSnowballItem::new);
    public static final DeferredItem<Item> IMPULSE_SNOWBALL = ITEMS.register("impulse_snowball", ImpulseSnowballItem::new);
    public static final DeferredItem<Item> CHERRY_BLOSSOM_SNOWBALL = ITEMS.register("cherry_blossom_snowball", CherryBlossomSnowballItem::new);
    public static final DeferredItem<Item> GHOST_SNOWBALL = ITEMS.register("ghost_snowball", GhostSnowballItem::new);

    public static final DeferredItem<Item> SNOWBALL_TANK = ITEMS.register("snowball_tank", () -> new SnowballTankItem());
    public static final DeferredItem<Item> LARGE_SNOWBALL_TANK = ITEMS.register("large_snowball_tank", LargeSnowballTankItem::new);

    public static final DeferredItem<Item> WOOD_SNOWBALL_CLAMP = ITEMS.register("wood_snowball_clamp", () -> new SnowballClampItem(Tiers.WOOD, 118));
    public static final DeferredItem<Item> STONE_SNOWBALL_CLAMP = ITEMS.register("stone_snowball_clamp", () -> new SnowballClampItem(Tiers.STONE, 260));
    public static final DeferredItem<Item> IRON_SNOWBALL_CLAMP = ITEMS.register("iron_snowball_clamp", () -> new SnowballClampItem(Tiers.IRON, 500));
    public static final DeferredItem<Item> GOLD_SNOWBALL_CLAMP = ITEMS.register("gold_snowball_clamp", () -> new SnowballClampItem(Tiers.GOLD, 64));
    public static final DeferredItem<Item> DIAMOND_SNOWBALL_CLAMP = ITEMS.register("diamond_snowball_clamp", () -> new SnowballClampItem(Tiers.DIAMOND, 3122));
    public static final DeferredItem<Item> NETHERITE_SNOWBALL_CLAMP = ITEMS.register("netherite_snowball_clamp", () -> new SnowballClampItem(Tiers.NETHERITE, 4062));
    public static final DeferredItem<Item> EMERALD_SNOWBALL_CLAMP = ITEMS.register("emerald_snowball_clamp", () -> new SnowballClampItem(BSFTiers.EMERALD, 2866));
    public static final DeferredItem<Item> SNOWBALL_CANNON = ITEMS.register("snowball_cannon", () -> new SnowballCannonItem());
    public static final DeferredItem<Item> POWERFUL_SNOWBALL_CANNON = ITEMS.register("powerful_snowball_cannon", PowerfulSnowballCannonItem::new);
    public static final DeferredItem<Item> FREEZING_SNOWBALL_CANNON = ITEMS.register("freezing_snowball_cannon", FreezingSnowballCannonItem::new);
    public static final DeferredItem<Item> IMPLOSION_SNOWBALL_CANNON = ITEMS.register("implosion_snowball_cannon", ImplosionSnowballCannonItem::new);
    public static final DeferredItem<Item> SNOWBALL_MACHINE_GUN = ITEMS.register("snowball_machine_gun", SnowballMachineGunItem::new);
    public static final DeferredItem<Item> SNOWBALL_SHOTGUN = ITEMS.register("snowball_shotgun", SnowballShotgunItem::new);
    public static final DeferredItem<Item> GLOVE = ITEMS.register("glove", GloveItem::new);
    public static final DeferredItem<Item> JEDI_GLOVE = ITEMS.register("jedi_glove", JediGloveItem::new);
    public static final DeferredItem<Item> REPULSIVE_FIELD_GENERATOR = ITEMS.register("repulsive_field_generator", RepulsiveFieldGeneratorItem::new);


    public static final DeferredItem<Item> ICE_SKATES_ITEM = ITEMS.register("ice_skates", IceSkatesItem::new);
    public static final DeferredItem<Item> SNOW_FALL_BOOTS = ITEMS.register("snow_fall_boots", SnowFallBootsItem::new);
    public static final DeferredItem<Item> SNOW_BLOCK_BLENDER = ITEMS.register("snow_block_blender", SnowBlockBlenderItem::new);
    public static final DeferredItem<Item> BASIN = ITEMS.register("basin", BasinItem::new);
    public static final DeferredItem<Item> SNOW_GOLEM_MODE_TWEAKER = ITEMS.register("snow_golem_mode_tweaker", SnowGolemModeTweakerItem::new);
    public static final DeferredItem<Item> CREATIVE_SNOW_GOLEM_TOOL = ITEMS.register("creative_snow_golem_tool", CreativeSnowGolemToolItem::new);
    public static final DeferredItem<Item> TARGET_LOCATOR = ITEMS.register("target_locator", TargetLocatorItem::new);
    public static final DeferredItem<Item> SNOW_TRAP_SETTER = ITEMS.register("snow_trap_setter", SnowTrapSetterItem::new);
    public static final DeferredItem<Item> SCULK_SNOWBALL_LAUNCHER = ITEMS.register("sculk_snowball_launcher", SculkSnowballLauncherItem::new);
    public static final DeferredItem<Item> SNOW_GOLEM_CONTAINER = ITEMS.register("snow_golem_container", SnowGolemContainer::new);
    public static final DeferredItem<Item> VECTOR_INVERSION_ANCHOR = ITEMS.register("vector_inversion_anchor", VectorInversionAnchorItem::new);
    public static final DeferredItem<Item> COLD_COMPRESSION_JET_ENGINE = ITEMS.register("cold_compression_jet_engine", ColdCompressionJetEngineItem::new);


    public static final DeferredItem<Item> POPSICLE = ITEMS.register("popsicle", PopsicleItem::new);
    public static final DeferredItem<Item> MILK_POPSICLE = ITEMS.register("milk_popsicle", MilkPopsicleItem::new);
    public static final DeferredItem<Item> VODKA = ITEMS.register("vodka", VodkaItem::new);
    public static final DeferredItem<Item> SUSPICIOUS_USB_FLASH_DRIVE = ITEMS.register("suspicious_usb_flash_drive", SuspiciousUSBFlashDriveItem::new);


    public static final DeferredItem<Item> SMART_SNOW_BLOCK = ITEMS.register("smart_snow_block", SmartSnowBlockItem::new);


    public static final DeferredItem<Item> SUPER_POWER_CORE = ITEMS.register("super_power_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SUPER_FROZEN_CORE = ITEMS.register("super_frozen_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> TRACKING_CORE = ITEMS.register("tracking_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> UNSTABLE_CORE = ITEMS.register("unstable_core", UnstableCoreItem::new);
    public static final DeferredItem<Item> REPULSION_CORE = ITEMS.register("repulsion_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> GRAVITY_CORE = ITEMS.register("gravity_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> SNOWBALL_CANNON_UPGRADE_SMITHING_TEMPLATE = ITEMS.register("snowball_cannon_upgrade_smithing_template", SnowballCannonUpgradeSmithingTemplateItem::new);
    public static final DeferredItem<Item> SNOWMAN_IN_HAND = ITEMS.register("snowman_in_hand", SnowmanInHandItem::new);

    public static final DeferredItem<Item> THRUST_GOLEM_CORE = ITEMS.register("thrust_golem_core", () -> new SnowGolemCoreItem(120, new String[]{"thrust_golem_core.tooltip","thrust_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> SWIFTNESS_GOLEM_CORE = ITEMS.register("swiftness_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"swiftness_golem_core.tooltip","swiftness_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> REGENERATION_GOLEM_CORE = ITEMS.register("regeneration_golem_core", () -> new SnowGolemCoreItem(100, new String[]{"regeneration_golem_core.tooltip","regeneration_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> CRITICAL_SNOW_GOLEM_CORE = ITEMS.register("critical_snow_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"critical_snow_golem_core.tooltip","critical_snow_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> REPULSIVE_FIELD_GOLEM_CORE = ITEMS.register("repulsive_field_golem_core", () -> new SnowGolemCoreItem(100, new String[]{"repulsive_field_golem_core.tooltip","repulsive_field_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> NEAR_TELEPORTATION_GOLEM_CORE = ITEMS.register("near_teleportation_golem_core", () -> new SnowGolemCoreItem(160, new String[]{"near_teleportation_golem_core.tooltip","near_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> ENDER_TELEPORTATION_GOLEM_CORE = ITEMS.register("ender_teleportation_golem_core", () -> new SnowGolemCoreItem(200, new String[]{"ender_teleportation_golem_core.tooltip","ender_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> ACTIVE_TELEPORTATION_GOLEM_CORE = ITEMS.register("active_teleportation_golem_core", () -> new SnowGolemCoreItem(240, new String[]{"active_teleportation_golem_core.tooltip","active_teleportation_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));
    public static final DeferredItem<Item> MOVEMENT_PREDICTION_GOLEM_CORE = ITEMS.register("movement_prediction_golem_core", () -> new SnowGolemCoreItem(0, new String[]{"movement_prediction_golem_core.tooltip","movement_prediction_golem_core_cd.tooltip"}, new ChatFormatting[]{ChatFormatting.BLUE,ChatFormatting.GRAY}));

    public static final DeferredItem<Item> SNOW_GOLEM_CORE_REMOVER = ITEMS.register("snow_golem_core_remover", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BLANK_GOLEM_CORE = ITEMS.register("blank_golem_core", () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> GPS_SNOWBALL = ITEMS.register("gps_snowball", () -> new Item(new Item.Properties()));    //This item does not need to be added to the group
    public static final DeferredItem<Item> SCULK_SNOWBALL = ITEMS.register("sculk_snowball", () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> WHITE_TEAM_LINKER = ITEMS.register("white_team_linker", () -> new TeamLinkerItem(0));
    public static final DeferredItem<Item> ORANGE_TEAM_LINKER = ITEMS.register("orange_team_linker", () -> new TeamLinkerItem(1));
    public static final DeferredItem<Item> MAGENTA_TEAM_LINKER = ITEMS.register("magenta_team_linker", () -> new TeamLinkerItem(2));
    public static final DeferredItem<Item> LIGHT_BLUE_TEAM_LINKER = ITEMS.register("light_blue_team_linker", () -> new TeamLinkerItem(3));
    public static final DeferredItem<Item> YELLOW_TEAM_LINKER = ITEMS.register("yellow_team_linker", () -> new TeamLinkerItem(4));
    public static final DeferredItem<Item> LIME_TEAM_LINKER = ITEMS.register("lime_team_linker", () -> new TeamLinkerItem(5));
    public static final DeferredItem<Item> PINK_TEAM_LINKER = ITEMS.register("pink_team_linker", () -> new TeamLinkerItem(6));
    public static final DeferredItem<Item> GRAY_TEAM_LINKER = ITEMS.register("gray_team_linker", () -> new TeamLinkerItem(7));
    public static final DeferredItem<Item> LIGHT_GRAY_TEAM_LINKER = ITEMS.register("light_gray_team_linker", () -> new TeamLinkerItem(8));
    public static final DeferredItem<Item> CYAN_TEAM_LINKER = ITEMS.register("cyan_team_linker", () -> new TeamLinkerItem(9));
    public static final DeferredItem<Item> PURPLE_TEAM_LINKER = ITEMS.register("purple_team_linker", () -> new TeamLinkerItem(10));
    public static final DeferredItem<Item> BLUE_TEAM_LINKER = ITEMS.register("blue_team_linker", () -> new TeamLinkerItem(11));
    public static final DeferredItem<Item> BROWN_TEAM_LINKER = ITEMS.register("brown_team_linker", () -> new TeamLinkerItem(12));
    public static final DeferredItem<Item> GREEN_TEAM_LINKER = ITEMS.register("green_team_linker", () -> new TeamLinkerItem(13));
    public static final DeferredItem<Item> RED_TEAM_LINKER = ITEMS.register("red_team_linker", () -> new TeamLinkerItem(14));
    public static final DeferredItem<Item> BLACK_TEAM_LINKER = ITEMS.register("black_team_linker", () -> new TeamLinkerItem(15));

    public static final DeferredItem<Item> REGION_TOOL = ITEMS.register("region_tool", RegionToolItem::new);
    public static final DeferredItem<Item> SCORING_DEVICE = ITEMS.register("scoring_device", ScoringDeviceItem::new);
    public static final DeferredItem<Item> VENDING_MACHINE = ITEMS.register("vending_machine", VendingMachineItem::new);
    public static final DeferredItem<Item> REGION_CONTROLLER = ITEMS.register("region_controller", RegionControllerItem::new);
    public static final DeferredItem<Item> REGION_CONTROLLER_VIEW = ITEMS.register("region_controller_view", RegionControllerViewItem::new);
    public static final DeferredItem<Item> REGION_PLAYER_INSPECTOR = ITEMS.register("region_player_inspector", RegionPlayerInspectorItem::new);
}
