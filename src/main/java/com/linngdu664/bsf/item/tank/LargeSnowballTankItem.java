package com.linngdu664.bsf.item.tank;

import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

public class LargeSnowballTankItem extends SnowballTankItem {
    public LargeSnowballTankItem() {
        super(new Properties().stacksTo(1).durability(192).rarity(Rarity.UNCOMMON));
    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        Item item = pStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
        if (!Items.AIR.equals(item)) {
            String path = BuiltInRegistries.ITEM.getKey(item).getPath();
            return MutableComponent.create(new TranslatableContents("item.bsf.large_" + path + "_tank", null, new Object[0]));
        }
        return super.getName(pStack);
    }
}
