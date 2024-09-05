package com.linngdu664.bsf.item.component;

import com.linngdu664.bsf.network.CustomStreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record UuidData(UUID uuid) {
    public static final Codec<UuidData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("uuid_high").forGetter(UuidData::getUuidHigh),
                    Codec.LONG.fieldOf("uuid_low").forGetter(UuidData::getUuidLow)
            ).apply(instance, UuidData::new)
    );
    public static final StreamCodec<ByteBuf, UuidData> STREAM_CODEC = StreamCodec.composite(
            CustomStreamCodecs.UUID_STREAM_CODEC, UuidData::uuid,
            UuidData::new
    );

    private long getUuidHigh() {
        return uuid.getMostSignificantBits();
    }

    private long getUuidLow() {
        return uuid.getLeastSignificantBits();
    }

    private UuidData(long high, long low) {
        this(new UUID(high, low));
    }
}
