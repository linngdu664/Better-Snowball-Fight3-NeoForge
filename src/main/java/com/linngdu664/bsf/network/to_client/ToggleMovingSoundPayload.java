package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.resources.sounds.MovingSoundInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ToggleMovingSoundPayload(int entityId, SoundEvent soundEvent, byte flag) implements CustomPacketPayload {
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
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(payload.entityId);
            if (entity != null) {
                SoundManager soundManager = Minecraft.getInstance().getSoundManager();
                if (payload.flag == PLAY_LOOP) {
                    MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent, true);
                    if (!soundManager.isActive(soundInstance)) {
                        soundManager.queueTickingSound(soundInstance);
                    }
                } else if (payload.flag == STOP_LOOP) {
                    MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent, true);
                    soundManager.stop(soundInstance);
                } else {
                    MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent, false);
                    soundManager.queueTickingSound(soundInstance);
                }
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
