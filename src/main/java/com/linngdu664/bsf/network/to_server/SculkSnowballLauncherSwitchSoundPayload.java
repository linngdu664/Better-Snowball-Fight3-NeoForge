package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SculkSnowballLauncherSwitchSoundPayload(boolean isIncrease) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SculkSnowballLauncherSwitchSoundPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("sculk_snowball_launcher_switch_sound"));
    public static final StreamCodec<ByteBuf, SculkSnowballLauncherSwitchSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SculkSnowballLauncherSwitchSoundPayload::isIncrease,
            SculkSnowballLauncherSwitchSoundPayload::new
    );

    public static void handleDataInServer(SculkSnowballLauncherSwitchSoundPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            ItemStack itemStack = sender.getItemInHand(InteractionHand.MAIN_HAND);
            if (itemStack.getItem().equals(ItemRegister.SCULK_SNOWBALL_LAUNCHER.get())) {
                int soundId = itemStack.getOrDefault(DataComponentRegister.SCULK_SOUND_ID, -1);
                if (payload.isIncrease) {
                    soundId = soundId + 1 == SoundRegister.MEME_SOUND_AMOUNT ? -1 : soundId + 1;
                } else {
                    soundId = soundId == -1 ? SoundRegister.MEME_SOUND_AMOUNT - 1 : soundId - 1;
                }
                itemStack.set(DataComponentRegister.SCULK_SOUND_ID, soundId);
                if (soundId == -1) {
                    itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new TranslatableContents("item.bsf.sculk_snowball_launcher", null, new Object[]{}))
                            .append(": ").append(MutableComponent.create(new TranslatableContents("random_sound.tip", null, new Object[]{}))));
                } else {
                    itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new TranslatableContents("item.bsf.sculk_snowball_launcher", null, new Object[]{}))
                            .append(": ").append(MutableComponent.create(new TranslatableContents("sound_id.tip", null, new Object[]{String.valueOf(soundId)}))));
                }
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
