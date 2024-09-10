package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ValueAdjustmentToolPayload(boolean isIncrease, boolean speedUp) implements CustomPacketPayload {
    public static final Type<ValueAdjustmentToolPayload> TYPE = new Type<>(Main.makeResLoc("value_adjustment_tool"));
    public static final StreamCodec<ByteBuf, ValueAdjustmentToolPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ValueAdjustmentToolPayload::isIncrease,
            ByteBufCodecs.BOOL, ValueAdjustmentToolPayload::speedUp,
            ValueAdjustmentToolPayload::new
    );

    public static void handleDataInServer(ValueAdjustmentToolPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            ItemStack itemStack = sender.getItemInHand(InteractionHand.MAIN_HAND);
            if (itemStack.getItem().equals(ItemRegister.VALUE_ADJUSTMENT_TOOL.get())) {
                int value = itemStack.getOrDefault(DataComponentRegister.GENERIC_INT_VALUE.get(), 0);
                if (payload.isIncrease) {
                    value += payload.speedUp ? 10 : 1;
                } else {
                    value -= payload.speedUp ? 10 : 1;
                }
                itemStack.set(DataComponentRegister.GENERIC_INT_VALUE.get(), value);
                itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("value: " + value)));
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
