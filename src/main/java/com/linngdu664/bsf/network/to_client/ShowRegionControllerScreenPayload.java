package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.to_client.handler.ShowRegionControllerScreenHandler;
import com.linngdu664.bsf.network.to_client.packed_paras.RegionControllerGuiParas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ShowRegionControllerScreenPayload(RegionControllerGuiParas paras) implements CustomPacketPayload  {
    public static final CustomPacketPayload.Type<ShowRegionControllerScreenPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("show_region_controller_screen"));

    public static final StreamCodec<ByteBuf, ShowRegionControllerScreenPayload> STREAM_CODEC = StreamCodec.composite(
            RegionControllerGuiParas.STREAM_CODEC, ShowRegionControllerScreenPayload::paras,
            ShowRegionControllerScreenPayload::new
    );

    public static void handleDataInClient(ShowRegionControllerScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ShowRegionControllerScreenHandler.handlePayload(payload));     // to prevent class loading
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
