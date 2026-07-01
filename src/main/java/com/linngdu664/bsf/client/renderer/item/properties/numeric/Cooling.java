package com.linngdu664.bsf.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class Cooling implements RangeSelectItemModelProperty {
    public static final MapCodec<Cooling> MAP_CODEC = MapCodec.unit(new Cooling());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        if (itemOwner != null && itemOwner.asLivingEntity() instanceof Player player) {
            return player.getCooldowns().getCooldownPercent(itemStack, 1);
        }
        return 0;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
