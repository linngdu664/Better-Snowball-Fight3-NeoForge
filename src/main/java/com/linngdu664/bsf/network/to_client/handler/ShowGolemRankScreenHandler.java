package com.linngdu664.bsf.network.to_client.handler;

import com.linngdu664.bsf.client.gui.screen.BSFSnowGolemRankScreen;
import com.linngdu664.bsf.network.to_client.ShowGolemRankScreenPayload;
import net.minecraft.client.Minecraft;

public class ShowGolemRankScreenHandler {
    public static void handlePayload(ShowGolemRankScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new BSFSnowGolemRankScreen(payload.id(), payload.rank(), payload.money()));
    }
}
