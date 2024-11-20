package com.linngdu664.bsf.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class VendingMachineScreenShower {
    public static void show(BlockPos pos, int rank, int price, boolean canSell) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new VendingMachineScreen(pos, String.valueOf(rank), String.valueOf(price), canSell));
    }
}
