package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.client.screenshake.ScreenshakeInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ScreenshakePayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ScreenshakePayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("screen_shake"));
    public final int duration;
    public float intensity1;
    public float intensity2;
    public float intensity3;
    public Easing intensityCurveStartEasing;
    public Easing intensityCurveEndEasing;

    public ScreenshakePayload(int duration) {
        this.intensityCurveStartEasing = Easing.LINEAR;
        this.intensityCurveEndEasing = Easing.LINEAR;
        this.duration = duration;
    }

    public ScreenshakePayload setIntensity(float intensity) {
        return this.setIntensity(intensity, intensity);
    }

    public ScreenshakePayload setIntensity(float intensity1, float intensity2) {
        return this.setIntensity(intensity1, intensity2, intensity2);
    }

    public ScreenshakePayload setIntensity(float intensity1, float intensity2, float intensity3) {
        this.intensity1 = intensity1;
        this.intensity2 = intensity2;
        this.intensity3 = intensity3;
        return this;
    }

    public ScreenshakePayload setEasing(Easing easing) {
        this.intensityCurveStartEasing = easing;
        this.intensityCurveEndEasing = easing;
        return this;
    }

    public ScreenshakePayload setEasing(Easing intensityCurveStartEasing, Easing intensityCurveEndEasing) {
        this.intensityCurveStartEasing = intensityCurveStartEasing;
        this.intensityCurveEndEasing = intensityCurveEndEasing;
        return this;
    }

    public static final StreamCodec<ByteBuf, ScreenshakePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ScreenshakePayload::getDuration,
            ByteBufCodecs.FLOAT, ScreenshakePayload::getIntensity1,
            ByteBufCodecs.FLOAT, ScreenshakePayload::getIntensity2,
            ByteBufCodecs.FLOAT, ScreenshakePayload::getIntensity3,
            ByteBufCodecs.STRING_UTF8, ScreenshakePayload::getIntensityCurveStartEasing,
            ByteBufCodecs.STRING_UTF8, ScreenshakePayload::getIntensityCurveEndEasing,
            ScreenshakePayload::new
    );

    private int getDuration() {
        return duration;
    }

    private float getIntensity1() {
        return intensity1;
    }

    private float getIntensity2() {
        return intensity2;
    }

    private float getIntensity3() {
        return intensity3;
    }

    private String getIntensityCurveStartEasing() {
        return intensityCurveStartEasing.name;
    }

    private String getIntensityCurveEndEasing() {
        return intensityCurveEndEasing.name;
    }

    private ScreenshakePayload(int duration, float intensity1, float intensity2, float intensity3, String intensityCurveStartEasing, String intensityCurveEndEasing) {
        this.duration = duration;
        this.intensity1 = intensity1;
        this.intensity2 = intensity2;
        this.intensity3 = intensity3;
        this.intensityCurveStartEasing = Easing.valueOf(intensityCurveStartEasing);
        this.intensityCurveEndEasing = Easing.valueOf(intensityCurveEndEasing);
    }

    public static void handleDataInClient(ScreenshakePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(payload.duration)).setIntensity(payload.intensity1, payload.intensity2, payload.intensity3).setEasing(payload.intensityCurveStartEasing, payload.intensityCurveEndEasing)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
