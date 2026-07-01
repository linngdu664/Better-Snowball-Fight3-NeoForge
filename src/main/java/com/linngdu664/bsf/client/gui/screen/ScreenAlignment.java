package com.linngdu664.bsf.client.gui.screen;

import net.minecraft.client.gui.components.StringWidget;

final class ScreenAlignment {
    private ScreenAlignment() {
    }

    static void alignRight(StringWidget widget, int x, int width) {
        widget.setMaxWidth(width);
        widget.setX(x + width - widget.getWidth());
    }

    static void alignCenter(StringWidget widget, int x, int width) {
        widget.setMaxWidth(width);
        widget.setX(x + (width - widget.getWidth()) / 2);
    }
}
