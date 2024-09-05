package com.linngdu664.bsf.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class BSFTiers {
    public static final SimpleTier EMERALD = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8, 3, 10, () -> Ingredient.of(Items.EMERALD));
}
