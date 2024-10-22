package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record VelocityInversePayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VelocityInversePayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("velocity_inverse"));
    public static final StreamCodec<ByteBuf, VelocityInversePayload> STREAM_CODEC = StreamCodec.unit(new VelocityInversePayload());

    public static void handleDataInClient(VelocityInversePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.player().setDeltaMovement(context.player().getDeltaMovement().reverse()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
