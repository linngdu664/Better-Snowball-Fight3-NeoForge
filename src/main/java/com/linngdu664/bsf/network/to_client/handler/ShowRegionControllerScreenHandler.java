package com.linngdu664.bsf.network.to_client.handler;

import com.linngdu664.bsf.client.gui.screen.RegionControllerScreen;
import com.linngdu664.bsf.network.to_client.ShowRegionControllerScreenPayload;
import net.minecraft.client.Minecraft;

public class ShowRegionControllerScreenHandler {
    public static void handlePayload(ShowRegionControllerScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new RegionControllerScreen(payload.paras()));
    }
}
