package com.linngdu664.bsf.client.renderer.item.properties.numeric;

import com.linngdu664.bsf.item.tool.ColdCompressionJetEngineItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ScStarting implements RangeSelectItemModelProperty {
    public static final MapCodec<ScStarting> MAP_CODEC = MapCodec.unit(new ScStarting());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        if (itemOwner == null) {
            return 0.0F;
        }
        LivingEntity livingEntity = itemOwner.asLivingEntity();
        if (livingEntity == null || livingEntity.getUseItem() != itemStack) {
            return 0.0F;
        }
        float pct = (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / ColdCompressionJetEngineItem.STARTUP_DURATION;
        return pct > 1.4F ? 2.0F : pct;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
