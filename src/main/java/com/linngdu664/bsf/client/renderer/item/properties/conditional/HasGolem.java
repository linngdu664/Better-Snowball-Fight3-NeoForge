package com.linngdu664.bsf.client.renderer.item.properties.conditional;

import com.linngdu664.bsf.registry.DataComponentRegister;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class HasGolem implements ConditionalItemModelProperty {
    public static final MapCodec<HasGolem> MAP_CODEC = MapCodec.unit(new HasGolem());

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA);
    }
}
