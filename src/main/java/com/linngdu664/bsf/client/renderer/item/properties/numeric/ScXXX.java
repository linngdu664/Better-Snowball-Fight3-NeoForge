package com.linngdu664.bsf.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ScXXX implements RangeSelectItemModelProperty {
    public static final MapCodec<ScXXX> MAP_CODEC = MapCodec.unit(new ScXXX());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        return ((float) itemStack.getMaxDamage() - itemStack.getDamageValue()) / itemStack.getMaxDamage();
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
