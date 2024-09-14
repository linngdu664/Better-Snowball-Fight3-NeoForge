package com.linngdu664.bsf.gui;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScoringGuiHandler {
    public static int score = 0;
    public static int hourMeter = 0;

    public static void tick() {
        if (hourMeter > 0) {
            hourMeter--;
        }
    }

    public static void set(int sc) {
        score = sc;
        hourMeter = 40;
    }

    public static int getBlend() {
        if (hourMeter < 20) {
            return Mth.lerpInt((float) hourMeter / 20, 0x00, 0xff) << 24;
        } else {
            return 0xff000000;
        }
    }
}
