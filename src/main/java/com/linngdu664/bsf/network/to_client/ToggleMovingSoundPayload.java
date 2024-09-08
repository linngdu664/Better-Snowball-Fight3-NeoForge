package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public record ToggleMovingSoundPayload(int entityId, SoundEvent soundEvent, byte flag) implements CustomPacketPayload {
    public static final ArrayList<ToggleMovingSoundPayload> SOUND_PAYLOADS = new ArrayList<>();
    public static final byte PLAY_ONCE = 0;
    public static final byte PLAY_LOOP = 1;
    public static final byte STOP_LOOP = 2;
    public static final CustomPacketPayload.Type<ToggleMovingSoundPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("toggle_moving_sound"));
    public static final StreamCodec<ByteBuf, ToggleMovingSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ToggleMovingSoundPayload::entityId,
            SoundEvent.DIRECT_STREAM_CODEC, ToggleMovingSoundPayload::soundEvent,
            ByteBufCodecs.BYTE, ToggleMovingSoundPayload::flag,
            ToggleMovingSoundPayload::new
    );

    public static void handleDataInClient(ToggleMovingSoundPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SOUND_PAYLOADS.add(payload));     // handle the payload in client tick event to prevent class loading
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
