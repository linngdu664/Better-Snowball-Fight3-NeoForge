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

public class ScreenShakePayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ScreenShakePayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("screen_shake"));
    public final int duration;
    public float intensity1;
    public float intensity2;
    public float intensity3;
    public Easing intensityCurveStartEasing;
    public Easing intensityCurveEndEasing;

    public ScreenShakePayload(int duration) {
        this.intensityCurveStartEasing = Easing.LINEAR;
        this.intensityCurveEndEasing = Easing.LINEAR;
        this.duration = duration;
    }

    public ScreenShakePayload setIntensity(float intensity) {
        return this.setIntensity(intensity, intensity);
    }

    public ScreenShakePayload setIntensity(float intensity1, float intensity2) {
        return this.setIntensity(intensity1, intensity2, intensity2);
    }

    public ScreenShakePayload setIntensity(float intensity1, float intensity2, float intensity3) {
        this.intensity1 = intensity1;
        this.intensity2 = intensity2;
        this.intensity3 = intensity3;
        return this;
    }

    public ScreenShakePayload setEasing(Easing easing) {
        this.intensityCurveStartEasing = easing;
        this.intensityCurveEndEasing = easing;
        return this;
    }

    public ScreenShakePayload setEasing(Easing intensityCurveStartEasing, Easing intensityCurveEndEasing) {
        this.intensityCurveStartEasing = intensityCurveStartEasing;
        this.intensityCurveEndEasing = intensityCurveEndEasing;
        return this;
    }

    public static final StreamCodec<ByteBuf, ScreenShakePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ScreenShakePayload::getDuration,
            ByteBufCodecs.FLOAT, ScreenShakePayload::getIntensity1,
            ByteBufCodecs.FLOAT, ScreenShakePayload::getIntensity2,
            ByteBufCodecs.FLOAT, ScreenShakePayload::getIntensity3,
            ByteBufCodecs.STRING_UTF8, ScreenShakePayload::getIntensityCurveStartEasing,
            ByteBufCodecs.STRING_UTF8, ScreenShakePayload::getIntensityCurveEndEasing,
            ScreenShakePayload::new
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

    private ScreenShakePayload(int duration, float intensity1, float intensity2, float intensity3, String intensityCurveStartEasing, String intensityCurveEndEasing) {
        this.duration = duration;
        this.intensity1 = intensity1;
        this.intensity2 = intensity2;
        this.intensity3 = intensity3;
        this.intensityCurveStartEasing = Easing.valueOf(intensityCurveStartEasing);
        this.intensityCurveEndEasing = Easing.valueOf(intensityCurveEndEasing);
    }

    public static void handleDataInClient(ScreenShakePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(payload.duration)).setIntensity(payload.intensity1, payload.intensity2, payload.intensity3).setEasing(payload.intensityCurveStartEasing, payload.intensityCurveEndEasing)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
