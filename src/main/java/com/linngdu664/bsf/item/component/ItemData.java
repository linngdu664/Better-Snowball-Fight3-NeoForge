package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record ItemData(Item item) {
    public static final Codec<ItemData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("item").forGetter(ItemData::getItemResLoc)
            ).apply(instance, ItemData::new)
    );
    public static final StreamCodec<ByteBuf, ItemData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ItemData::getItemResLoc,
            ItemData::new
    );
    public static final ItemData EMPTY = new ItemData(Items.AIR);

    private ItemData(String itemName) {
        this(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemName)));
    }

    private String getItemResLoc() {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

}
