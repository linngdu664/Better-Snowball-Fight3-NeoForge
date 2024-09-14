package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.item.component.IntegerGroupData;
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
                int index = itemStack.getOrDefault(DataComponentRegister.SELECTED_INDEX.get(), 0);
                IntegerGroupData group = itemStack.getOrDefault(DataComponentRegister.INTEGER_GROUP.get(), IntegerGroupData.EMPTY);
                if (payload.isIncrease) {
                    switch (index) {
                        case 0:
                            group = new IntegerGroupData(group.val1() + (payload.speedUp ? 10 : 1), group.val2(), group.val3(), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val1: " + group.val1())));
                            break;
                        case 1:
                            group = new IntegerGroupData(group.val1(), group.val2() + (payload.speedUp ? 10 : 1), group.val3(), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val2: " + group.val2())));
                            break;
                        case 2:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3() + (payload.speedUp ? 10 : 1), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val3: " + group.val3())));
                            break;
                        case 3:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3(), group.val4() + (payload.speedUp ? 10 : 1), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val4: " + group.val4())));
                            break;
                        default:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3(), group.val4(), group.val5() + (payload.speedUp ? 10 : 1));
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val5: " + group.val5())));
                    }
                } else {
                    switch (index) {
                        case 0:
                            group = new IntegerGroupData(group.val1() - (payload.speedUp ? 10 : 1), group.val2(), group.val3(), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val1: " + group.val1())));
                            break;
                        case 1:
                            group = new IntegerGroupData(group.val1(), group.val2() - (payload.speedUp ? 10 : 1), group.val3(), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val2: " + group.val2())));
                            break;
                        case 2:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3() - (payload.speedUp ? 10 : 1), group.val4(), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val3: " + group.val3())));
                            break;
                        case 3:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3(), group.val4() - (payload.speedUp ? 10 : 1), group.val5());
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val4: " + group.val4())));
                            break;
                        default:
                            group = new IntegerGroupData(group.val1(), group.val2(), group.val3(), group.val4(), group.val5() - (payload.speedUp ? 10 : 1));
                            itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val5: " + group.val5())));
                    }
                }
                itemStack.set(DataComponentRegister.INTEGER_GROUP.get(), group);
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
