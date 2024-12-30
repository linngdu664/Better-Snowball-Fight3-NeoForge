package com.linngdu664.bsf.network.to_client.handler;

import com.linngdu664.bsf.client.gui.screen.RegionPlayerInspectorScreen;
import com.linngdu664.bsf.network.to_client.ShowRegionPlayerInspectorScreenPayload;
import net.minecraft.client.Minecraft;

public class ShowRegionPlayerInspectorScreenHandler {
    public static void handlePayload(ShowRegionPlayerInspectorScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new RegionPlayerInspectorScreen(payload.blockPos(), payload.region(), payload.kickPos(), payload.permittedTeams(), payload.checkItem(), payload.checkTeam()));
    }
}
