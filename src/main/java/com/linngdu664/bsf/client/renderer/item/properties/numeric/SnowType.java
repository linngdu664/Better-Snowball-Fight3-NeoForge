package com.linngdu664.bsf.client.renderer.item.properties.numeric;

import com.linngdu664.bsf.registry.DataComponentRegister;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class SnowType implements RangeSelectItemModelProperty {
    public static final MapCodec<SnowType> MAP_CODEC = MapCodec.unit(new SnowType());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        return itemStack.getOrDefault(DataComponentRegister.BASIN_SNOW_TYPE, (byte) 0);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
