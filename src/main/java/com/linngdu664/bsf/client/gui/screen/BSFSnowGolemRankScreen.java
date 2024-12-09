package com.linngdu664.bsf.client.gui.screen;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.network.to_server.UpdateGolemRankPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class BSFSnowGolemRankScreen extends Screen {
    private static final int MIDDLE_MAX_WIDTH = 350;
    private static final int MIDDLE_HEIGHT = 25 * 2;  // 2个输入框
    private static final int LABEL_INPUT_GAP = 4;
    private static final int MAIN_MARGIN = 9;
    private final int id;
    private final Component rankComponent = Component.translatable("gui.bsf.rank");
    private final Component moneyComponent = Component.translatable("gui.bsf.money");
    private EditBox rankEdit;
    private EditBox moneyEdit;
    private String rankStr;
    private String moneyStr;

    public BSFSnowGolemRankScreen(int id, int rank, int money) {
        super(Component.literal(""));
        this.id = id;
        this.rankStr = String.valueOf(rank);
        this.moneyStr = String.valueOf(money);
    }

    @Override
    protected void init() {
        super.init();
        Font font = minecraft.font;
        // 确定各组件位置
        int beginY = height / 2 - MIDDLE_HEIGHT / 2;
        int maxComponentWidth = Math.max(font.width(rankComponent), font.width(moneyComponent));
        int middleWidth = Math.min(MIDDLE_MAX_WIDTH, width - 2 * MAIN_MARGIN);
        int labelBeginX = width / 2 - middleWidth / 2;
        int inputEndX = width / 2 + middleWidth / 2;
        int labelWidth = Math.min(maxComponentWidth, (middleWidth - LABEL_INPUT_GAP) / 2);
        int inputBeginX = labelBeginX + labelWidth + LABEL_INPUT_GAP;
        int inputWidth = inputEndX - inputBeginX;

        StringWidget rankLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 6, labelWidth, 9, rankComponent, font));
        StringWidget moneyLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 31, labelWidth, 9, moneyComponent, font));
        rankLabel.alignRight();
        moneyLabel.alignRight();

        rankEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY, inputWidth, 20, rankComponent));
        moneyEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY + 25, inputWidth, 20, moneyComponent));
        rankEdit.setValue(rankStr);
        moneyEdit.setValue(moneyStr);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.level.getEntity(id) instanceof BSFSnowGolemEntity) {
            try {
                PacketDistributor.sendToServer(new UpdateGolemRankPayload(id, Integer.parseInt(rankEdit.getValue()), Integer.parseInt(moneyEdit.getValue())));
            } catch (NumberFormatException ignore) {

            }
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        rankStr = rankEdit.getValue();
        moneyStr = moneyEdit.getValue();
        super.resize(minecraft, width, height);
    }
}
