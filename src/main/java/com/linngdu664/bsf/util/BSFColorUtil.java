package com.linngdu664.bsf.util;

import net.minecraft.world.item.DyeColor;

public class BSFColorUtil {
    public static String getColorNameKeyById(int id) {
        return "color.minecraft." + DyeColor.byId(id).getName();
    }

    public static String getColorTransNameById(int id) {
        return BSFCommonUtil.getTransStr(getColorNameKeyById(id));
    }
}
