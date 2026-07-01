package com.linngdu664.bsf.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public abstract class AbstractBSFEnhanceableToolItem extends Item {
    public AbstractBSFEnhanceableToolItem(Rarity rarity, int durability) {
        this(com.linngdu664.bsf.Main.itemProperties().stacksTo(1).rarity(rarity).durability(durability).repairable(Items.IRON_INGOT).enchantable(1));
    }

    protected AbstractBSFEnhanceableToolItem(Rarity rarity, int durability, TagKey<Item> repairItems) {
        this(com.linngdu664.bsf.Main.itemProperties().stacksTo(1).rarity(rarity).durability(durability).repairable(repairItems).enchantable(1));
    }

    protected AbstractBSFEnhanceableToolItem(Properties properties) {
        super(properties);
    }
}

