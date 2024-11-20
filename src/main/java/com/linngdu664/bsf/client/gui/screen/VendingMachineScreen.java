package com.linngdu664.bsf.client.gui.screen;

import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.network.to_server.UpdateVendingMachinePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VendingMachineScreen extends Screen {
    private static final int MIDDLE_MAX_WIDTH = 350;
    private static final int MIDDLE_HEIGHT = 2 * 25 + 10 + 17;  // 2输入框+间隔+1复选框
    private static final int LABEL_INPUT_GAP = 4;
    private static final int MAIN_MARGIN = 9;
    private final BlockPos blockPos;
    private final Component rankComponent = Component.translatable("gui.bsf.minimum_rank");
    private final Component priceComponent = Component.translatable("gui.bsf.price");
    private final Component canSellComponent = Component.translatable("gui.bsf.allow_refunds");
    private EditBox rankEdit;
    private EditBox priceEdit;
    private Checkbox canSellCheckbox;
    private String rankStr;
    private String priceStr;
    private boolean canSell;

    public VendingMachineScreen(BlockPos blockPos, String rankStr, String priceStr, boolean canSell) {
        super(Component.literal(""));
        this.blockPos = blockPos;
        this.rankStr = rankStr;
        this.priceStr = priceStr;
        this.canSell = canSell;
    }

    @Override
    protected void init() {
        super.init();
        Font font = minecraft.font;

        // 确定各组件位置
        int beginY = height / 2 - MIDDLE_HEIGHT / 2;
        int maxComponentWidth = Math.max(font.width(rankComponent), font.width(priceComponent));
        int middleWidth = Math.min(MIDDLE_MAX_WIDTH, width - 2 * MAIN_MARGIN);
        int labelBeginX = width / 2 - middleWidth / 2;
        int inputEndX = width / 2 + middleWidth / 2;
        int labelWidth = Math.min(maxComponentWidth, (middleWidth - LABEL_INPUT_GAP) / 2);
        int inputBeginX = labelBeginX + labelWidth + LABEL_INPUT_GAP;
        int inputWidth = inputEndX - inputBeginX;

        StringWidget rankLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 6, labelWidth, 9, rankComponent, font));
        StringWidget priceLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 31, labelWidth, 9, priceComponent, font));
        rankLabel.alignRight();
        priceLabel.alignRight();

        rankEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY, inputWidth, 20, rankComponent));
        priceEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY + 25, inputWidth, 20, priceComponent));
        rankEdit.setValue(rankStr);
        priceEdit.setValue(priceStr);

        int checkboxWidth = 21 + font.width(canSellComponent);
        canSellCheckbox = addRenderableWidget(Checkbox.builder(canSellComponent, font).pos(width / 2 - checkboxWidth / 2, beginY + 60).selected(canSell).build());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        rankStr = rankEdit.getValue();
        priceStr = priceEdit.getValue();
        canSell = canSellCheckbox.selected();
        super.resize(minecraft, width, height);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.level.getBlockEntity(blockPos) instanceof VendingMachineBlockEntity) {
            // 发包
            try {
                PacketDistributor.sendToServer(new UpdateVendingMachinePayload(
                        blockPos,
                        Integer.parseInt(rankEdit.getValue()),
                        Integer.parseInt(priceEdit.getValue()),
                        canSellCheckbox.selected()
                ));
            } catch (NumberFormatException ignore) {
            }
        }
    }
}
