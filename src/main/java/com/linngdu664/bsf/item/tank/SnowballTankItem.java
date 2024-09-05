package com.linngdu664.bsf.item.tank;

import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowballTankItem extends Item {
    public SnowballTankItem() {
        super(new Properties().stacksTo(1).durability(96).rarity(Rarity.UNCOMMON));
    }

    public SnowballTankItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.getOffhandItem().isEmpty()) {
            Item item = itemStack.getOrDefault(DataComponentRegister.SNOWBALL_TANK_TYPE, ItemData.EMPTY).item();
            if (!Items.AIR.equals(item)) {
                int damageValue = itemStack.getDamageValue();
                int maxDamage = itemStack.getMaxDamage();
                Inventory inventory = pPlayer.getInventory();
                if (pPlayer.isShiftKeyDown() || damageValue >= maxDamage - 16) {
                    int k = maxDamage - damageValue;
                    itemStack.setDamageValue(maxDamage);
                    if (!pPlayer.getAbilities().instabuild) {
                        itemStack.remove(DataComponentRegister.SNOWBALL_TANK_TYPE);
                    }
                    for (int i = 0; i < k / 16; i++) {
                        inventory.placeItemBackInInventory(new ItemStack(item, 16), true);
                    }
                    inventory.placeItemBackInInventory(new ItemStack(item, k % 16), true);
                } else {
                    itemStack.setDamageValue(damageValue + 16);
                    inventory.placeItemBackInInventory(new ItemStack(item, 16), true);
                }
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(itemStack);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        Item item = pStack.getOrDefault(DataComponentRegister.SNOWBALL_TANK_TYPE, ItemData.EMPTY).item();
        if (!Items.AIR.equals(item)) {
            String path = BuiltInRegistries.ITEM.getKey(item).getPath();
            return MutableComponent.create(new TranslatableContents("item.bsf." + path + "_tank", null, new Object[0]));
        }
        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Item item = stack.getOrDefault(DataComponentRegister.SNOWBALL_TANK_TYPE, ItemData.EMPTY).item();
        if (item instanceof AbstractBSFSnowballItem snowballItem) {
            snowballItem.generateWeaponTips(tooltipComponents);
            snowballItem.addLastTips(tooltipComponents);
        } else {
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_storage_tank.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_storage_tank1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_storage_tank2.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_storage_tank3.tooltip", null, new Object[]{Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }
}
