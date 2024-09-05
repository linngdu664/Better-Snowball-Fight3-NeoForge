package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.client.model.SnowFallBootsModel;
import com.linngdu664.bsf.registry.ArmorMaterialRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SnowFallBootsItem extends ArmorItem {
    public SnowFallBootsItem() {
        super(ArmorMaterialRegister.SNOW_FALL_BOOTS_ARMOR_MATERIAL, Type.BOOTS, new Properties().rarity(Rarity.UNCOMMON).stacksTo(1).durability(810));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(), Map.of(
                        "left_leg", new SnowFallBootsModel(Minecraft.getInstance().getEntityModels().bakeLayer(SnowFallBootsModel.LAYER_LOCATION)).bone,
                        "right_leg", new SnowFallBootsModel(Minecraft.getInstance().getEntityModels().bakeLayer(SnowFallBootsModel.LAYER_LOCATION)).bone,
                        "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                        "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                armorModel.crouching = livingEntity.isShiftKeyDown();
                armorModel.riding = original.riding;
                armorModel.young = livingEntity.isBaby();
                return armorModel;
            }
        });
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pStack, ItemStack pRepairCandidate) {
        return pRepairCandidate.is(Items.LEATHER_BOOTS);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snow_fall_boots.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snow_fall_boots1.tooltip", null, new Object[]{MutableComponent.create(new TranslatableContents("enchantment.bsf.kinetic_energy_storage",null,new Object[0])).getString()})).withStyle(ChatFormatting.GRAY));
    }
}
