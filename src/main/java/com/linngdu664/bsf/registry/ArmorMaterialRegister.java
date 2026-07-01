package com.linngdu664.bsf.registry;

import com.google.common.collect.Maps;
import com.linngdu664.bsf.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class ArmorMaterialRegister {
    private static final TagKey<Item> EMPTY_REPAIR = TagKey.create(Registries.ITEM, Main.makeResLoc("none"));
    private static final ResourceKey<EquipmentAsset> ICE_SKATES_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Main.makeResLoc("ice_skates"));
    private static final ResourceKey<EquipmentAsset> SNOW_FALL_BOOTS_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Main.makeResLoc("snow_fall_boots"));

    public static final ArmorMaterial ICE_SKATES_ARMOR_MATERIAL = new ArmorMaterial(5, makeDefense(1, 2, 3, 1, 3), 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, EMPTY_REPAIR, ICE_SKATES_ASSET);
    public static final ArmorMaterial SNOW_FALL_BOOTS_ARMOR_MATERIAL = new ArmorMaterial(16, makeDefense(1, 2, 3, 1, 3), 17, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, EMPTY_REPAIR, SNOW_FALL_BOOTS_ASSET);

    private static Map<ArmorType, Integer> makeDefense(int boots, int leggings, int chestplate, int helmet, int body) {
        return Maps.newEnumMap(Map.of(
                ArmorType.BOOTS, boots,
                ArmorType.LEGGINGS, leggings,
                ArmorType.CHESTPLATE, chestplate,
                ArmorType.HELMET, helmet,
                ArmorType.BODY, body
        ));
    }
}
