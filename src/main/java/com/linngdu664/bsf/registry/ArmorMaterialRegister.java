package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ArmorMaterialRegister {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Main.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ICE_SKATES_ARMOR_MATERIAL = register("ice_skates", Util.make(new EnumMap(ArmorItem.Type.class), (p_323383_) -> {
        p_323383_.put(ArmorItem.Type.BOOTS, 1);
        p_323383_.put(ArmorItem.Type.LEGGINGS, 2);
        p_323383_.put(ArmorItem.Type.CHESTPLATE, 3);
        p_323383_.put(ArmorItem.Type.HELMET, 1);
        p_323383_.put(ArmorItem.Type.BODY, 3);
    }), 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SNOW_FALL_BOOTS_ARMOR_MATERIAL = register("snow_fall_boots", Util.make(new EnumMap(ArmorItem.Type.class), (p_323383_) -> {
        p_323383_.put(ArmorItem.Type.BOOTS, 1);
        p_323383_.put(ArmorItem.Type.LEGGINGS, 2);
        p_323383_.put(ArmorItem.Type.CHESTPLATE, 3);
        p_323383_.put(ArmorItem.Type.HELMET, 1);
        p_323383_.put(ArmorItem.Type.BODY, 3);
    }), 17, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Main.makeResLoc(name)));
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);
        ArmorItem.Type[] var9 = ArmorItem.Type.values();
        for (ArmorItem.Type armoritem$type : var9) {
            enummap.put(armoritem$type, defense.get(armoritem$type));
        }
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance));
    }
}
