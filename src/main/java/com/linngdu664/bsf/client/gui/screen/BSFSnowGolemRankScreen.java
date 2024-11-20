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
    private static final int MIDDLE_HEIGHT = 25;  // 1个输入框
    private static final int LABEL_INPUT_GAP = 4;
    private static final int MAIN_MARGIN = 9;
    private final int id;
    private final Component rankComponent = Component.translatable("gui.bsf.rank");
    private EditBox rankEdit;
    private String rankStr;

    public BSFSnowGolemRankScreen(int id, int rank) {
        super(Component.literal(""));
        this.id = id;
        this.rankStr = String.valueOf(rank);
    }

    @Override
    protected void init() {
        super.init();
        Font font = minecraft.font;
        // 确定各组件位置
        int beginY = height / 2 - MIDDLE_HEIGHT / 2;
        int maxComponentWidth = font.width(rankComponent);
        int middleWidth = Math.min(MIDDLE_MAX_WIDTH, width - 2 * MAIN_MARGIN);
        int labelBeginX = width / 2 - middleWidth / 2;
        int inputEndX = width / 2 + middleWidth / 2;
        int labelWidth = Math.min(maxComponentWidth, (middleWidth - LABEL_INPUT_GAP) / 2);
        int inputBeginX = labelBeginX + labelWidth + LABEL_INPUT_GAP;
        int inputWidth = inputEndX - inputBeginX;

        StringWidget rankLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 6, labelWidth, 9, rankComponent, font));
        rankLabel.alignRight();

        rankEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY, inputWidth, 20, rankComponent));
        rankEdit.setValue(rankStr);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.level.getEntity(id) instanceof BSFSnowGolemEntity) {
            try {
                PacketDistributor.sendToServer(new UpdateGolemRankPayload(id, Integer.parseInt(rankEdit.getValue())));
            } catch (NumberFormatException ignore) {

            }
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        rankStr = rankEdit.getValue();
        super.resize(minecraft, width, height);
    }
}
