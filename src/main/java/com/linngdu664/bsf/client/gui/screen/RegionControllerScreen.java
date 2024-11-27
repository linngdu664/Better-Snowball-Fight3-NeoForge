package com.linngdu664.bsf.client.gui.screen;

import com.linngdu664.bsf.block.entity.RegionControllerBlockEntity;
import com.linngdu664.bsf.network.to_client.packed_paras.RegionControllerGuiParas;
import com.linngdu664.bsf.network.to_server.UpdateRegionControllerPayload;
import com.linngdu664.bsf.network.to_server.packed_paras.UpdateRegionControllerParas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class RegionControllerScreen extends Screen {
    private static final int MAIN_MAX_WIDTH = 420;
    private static final int MAIN_MARGIN = 9;
    private static final int MAIN_HEIGHT = 3 * 10 + 15 + 5 * 25;  // 3文本+间隔+5行输入框
    private static final int LABEL_INPUT_GAP = 4;
    private static final int LEFT_RIGHT_GAP = 15;
    private final BlockPos blockPos;
    private final Component regionComponent;
    private final Component spawnsComponent;
    private final Component golemsComponent;
    private final Component spawnBlockComponent = Component.translatable("gui.bsf.block_of_spawn_points");
    private final Component playerMultiplierComponent = Component.translatable("gui.bsf.player_multiplier");
    private final Component golemMultiplierComponent = Component.translatable("gui.bsf.golem_multiplier");
    private final Component diversityComponent = Component.translatable("gui.bsf.diversity");
    private final Component rankOffsetComponent = Component.translatable("gui.bsf.rank_offset");
    private final Component fastestStrengthComponent = Component.translatable("gui.bsf.fastest_strength");
    private final Component slowestStrengthComponent = Component.translatable("gui.bsf.slowest_strength");
    private final Component enemyTeamNumComponent = Component.translatable("gui.bsf.number_of_enemy_teams");
    private final Component maxGolemNumComponent = Component.translatable("gui.bsf.maximum_number_of_golems");
    private EditBox spawnBlockEdit;
    private EditBox playerMultiplierEdit;
    private EditBox golemMultiplierEdit;
    private EditBox diversityEdit;
    private EditBox rankOffsetEdit;
    private EditBox fastestStrengthEdit;
    private EditBox slowestStrengthEdit;
    private EditBox enemyTeamNumEdit;
    private EditBox maxGolemNumEdit;
    private String spawnBlockStr;
    private String playerMultiplierStr;
    private String golemMultiplierStr;
    private String diversityStr;
    private String rankOffsetStr;
    private String fastestStrengthStr;
    private String slowestStrengthStr;
    private String enemyTeamNumStr;
    private String maxGolemNumStr;

    public RegionControllerScreen(RegionControllerGuiParas paras) {
        super(Component.literal(""));
        this.blockPos = paras.blockPos();
        BlockPos start = paras.region().start();
        BlockPos end = paras.region().end();
        this.regionComponent = Component.translatable("gui.bsf.battle_region_tip", start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
        this.spawnsComponent = Component.translatable("gui.bsf.number_of_spawn_points_tip", paras.spawnNum());
        this.golemsComponent = Component.translatable("gui.bsf.types_of_snow_golems_tip", paras.golemNum());
        this.spawnBlockStr = paras.spawnBlock();
        this.playerMultiplierStr = String.valueOf(paras.playerMultiplier());
        this.golemMultiplierStr = String.valueOf(paras.golemMultiplier());
        this.diversityStr = String.valueOf(paras.diversity());
        this.rankOffsetStr = String.valueOf(paras.rankOffset());
        this.fastestStrengthStr = String.valueOf(paras.fastestStrength());
        this.slowestStrengthStr = String.valueOf(paras.slowestStrength());
        this.enemyTeamNumStr = String.valueOf(paras.enemyTeamNum());
        this.maxGolemNumStr = String.valueOf(paras.maxGolem());
    }

    // 设置组件
    @Override
    protected void init() {
        super.init();
        Font font = minecraft.font;

        // 确定各组件位置
        int beginY = height / 2 - MAIN_HEIGHT / 2;
//        int maxComponentWidth = Math.max(font.width(spawnBlockComponent), Math.max(font.width(playerMultiplierComponent), Math.max(font.width(golemMultiplierComponent), Math.max(font.width(diversityComponent), Math.max(font.width(enemyTeamNumComponent), font.width(maxGolemNumComponent))))));
        int maxLeftComponentWidth = Math.max(font.width(spawnBlockComponent), Math.max(font.width(playerMultiplierComponent), Math.max(font.width(diversityComponent), Math.max(font.width(fastestStrengthComponent), font.width(enemyTeamNumComponent)))));
        int maxRightComponentWidth = Math.max(font.width(golemMultiplierComponent), Math.max(font.width(rankOffsetComponent), Math.max(font.width(slowestStrengthComponent), font.width(maxGolemNumComponent))));
        int mainWidth = Math.min(MAIN_MAX_WIDTH, width - 2 * MAIN_MARGIN);
        int leftLabelBeginX = width / 2 - mainWidth / 2;
        int leftLabelWidth = Math.min(maxLeftComponentWidth, (mainWidth / 2 - LEFT_RIGHT_GAP / 2 - LABEL_INPUT_GAP) / 2);
        int leftInputBeginX = leftLabelBeginX + leftLabelWidth + LABEL_INPUT_GAP;
        int leftInputWidth = width / 2 - LEFT_RIGHT_GAP / 2 - leftInputBeginX;
        int rightLabelBeginX = width / 2 + LEFT_RIGHT_GAP / 2;
        int rightLabelWidth = Math.min(maxRightComponentWidth, (mainWidth / 2 - LEFT_RIGHT_GAP / 2 - LABEL_INPUT_GAP) / 2);
        int rightInputBeginX = rightLabelBeginX + rightLabelWidth + LABEL_INPUT_GAP;
        int rightInputWidth = width / 2 + mainWidth / 2 - rightInputBeginX;
        int fullInputWidth = width / 2 + mainWidth / 2 - leftInputBeginX;
//        int labelBeginX = width / 2 - mainWidth / 2;
//        int inputEndX = width / 2 + mainWidth / 2;
//        int labelWidth = Math.min(maxComponentWidth, (mainWidth - LABEL_INPUT_GAP) / 2);
//        int inputBeginX = labelBeginX + labelWidth + LABEL_INPUT_GAP;
//        int inputWidth = inputEndX - inputBeginX;

        StringWidget regionLabel = addRenderableWidget(new StringWidget(0, beginY, width, 9, regionComponent, font));
        StringWidget spawnsLabel = addRenderableWidget(new StringWidget(0, beginY + 10, width, 9, spawnsComponent, font));
        StringWidget golemsLabel = addRenderableWidget(new StringWidget(0, beginY + 20, width, 9, golemsComponent, font));
        regionLabel.alignCenter();
        spawnsLabel.alignCenter();
        golemsLabel.alignCenter();

        StringWidget spawnBlockLabel = addRenderableWidget(new StringWidget(leftLabelBeginX, beginY + 51, leftLabelWidth, 9, spawnBlockComponent, font));
        StringWidget playerMultiplierLabel = addRenderableWidget(new StringWidget(leftLabelBeginX, beginY + 76, leftLabelWidth, 9, playerMultiplierComponent, font));
        StringWidget golemMultiplierLabel = addRenderableWidget(new StringWidget(rightLabelBeginX, beginY + 76, rightLabelWidth, 9, golemMultiplierComponent, font));
        StringWidget diversityLabel = addRenderableWidget(new StringWidget(leftLabelBeginX, beginY + 101, leftLabelWidth, 9, diversityComponent, font));
        StringWidget rankOffsetLabel = addRenderableWidget(new StringWidget(rightLabelBeginX, beginY + 101, rightLabelWidth, 9, rankOffsetComponent, font));
        StringWidget fastestStrengthLabel = addRenderableWidget(new StringWidget(leftLabelBeginX, beginY + 126, leftLabelWidth, 9, fastestStrengthComponent, font));
        StringWidget slowestStrengthLabel = addRenderableWidget(new StringWidget(rightLabelBeginX, beginY + 126, rightLabelWidth, 9, slowestStrengthComponent, font));
        StringWidget enemyTeamNumLabel = addRenderableWidget(new StringWidget(leftLabelBeginX, beginY + 151, leftLabelWidth, 9, enemyTeamNumComponent, font));
        StringWidget maxGolemNumLabel = addRenderableWidget(new StringWidget(rightLabelBeginX, beginY + 151, rightLabelWidth, 9, maxGolemNumComponent, font));
        spawnBlockLabel.alignRight();
        playerMultiplierLabel.alignRight();
        golemMultiplierLabel.alignRight();
        diversityLabel.alignRight();
        rankOffsetLabel.alignRight();
        fastestStrengthLabel.alignRight();
        slowestStrengthLabel.alignRight();
        enemyTeamNumLabel.alignRight();
        maxGolemNumLabel.alignRight();

        spawnBlockEdit = addRenderableWidget(new EditBox(font, leftInputBeginX, beginY + 45, fullInputWidth, 20, spawnBlockComponent));
        playerMultiplierEdit = addRenderableWidget(new EditBox(font, leftInputBeginX, beginY + 70, leftInputWidth, 20, playerMultiplierComponent));
        golemMultiplierEdit = addRenderableWidget(new EditBox(font, rightInputBeginX, beginY + 70, rightInputWidth, 20, golemMultiplierComponent));
        diversityEdit = addRenderableWidget(new EditBox(font, leftInputBeginX, beginY + 95, leftInputWidth, 20, diversityComponent));
        rankOffsetEdit = addRenderableWidget(new EditBox(font, rightInputBeginX, beginY + 95, rightInputWidth, 20, rankOffsetComponent));
        fastestStrengthEdit = addRenderableWidget(new EditBox(font, leftInputBeginX, beginY + 120, leftInputWidth, 20, fastestStrengthComponent));
        slowestStrengthEdit = addRenderableWidget(new EditBox(font, rightInputBeginX, beginY + 120, rightInputWidth, 20, slowestStrengthComponent));
        enemyTeamNumEdit = addRenderableWidget(new EditBox(font, leftInputBeginX, beginY + 145, leftInputWidth, 20, enemyTeamNumComponent));
        maxGolemNumEdit = addRenderableWidget(new EditBox(font, rightInputBeginX, beginY + 145, rightInputWidth, 20, maxGolemNumComponent));
        spawnBlockEdit.setValue(spawnBlockStr);
        playerMultiplierEdit.setValue(playerMultiplierStr);
        golemMultiplierEdit.setValue(golemMultiplierStr);
        diversityEdit.setValue(diversityStr);
        rankOffsetEdit.setValue(rankOffsetStr);
        fastestStrengthEdit.setValue(fastestStrengthStr);
        slowestStrengthEdit.setValue(slowestStrengthStr);
        enemyTeamNumEdit.setValue(enemyTeamNumStr);
        maxGolemNumEdit.setValue(maxGolemNumStr);
    }

    // 关闭GUI，向服务器发送参数
    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.level.getBlockEntity(blockPos) instanceof RegionControllerBlockEntity) {
            // 发包
            try {
                PacketDistributor.sendToServer(new UpdateRegionControllerPayload(new UpdateRegionControllerParas(
                        blockPos,
                        spawnBlockEdit.getValue(),
                        Float.parseFloat(playerMultiplierEdit.getValue()),
                        Float.parseFloat(golemMultiplierEdit.getValue()),
                        Float.parseFloat(diversityEdit.getValue()),
                        Float.parseFloat(rankOffsetEdit.getValue()),
                        Float.parseFloat(fastestStrengthEdit.getValue()),
                        Float.parseFloat(slowestStrengthEdit.getValue()),
                        Integer.parseInt(enemyTeamNumEdit.getValue()),
                        Integer.parseInt(maxGolemNumEdit.getValue())
                )));
            } catch (NumberFormatException ignore) {
            }
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        spawnBlockStr = spawnBlockEdit.getValue();
        playerMultiplierStr = playerMultiplierEdit.getValue();
        golemMultiplierStr = golemMultiplierEdit.getValue();
        diversityStr = diversityEdit.getValue();
        rankOffsetStr = rankOffsetEdit.getValue();
        fastestStrengthStr = fastestStrengthEdit.getValue();
        slowestStrengthStr = slowestStrengthEdit.getValue();
        enemyTeamNumStr = enemyTeamNumEdit.getValue();
        maxGolemNumStr = maxGolemNumEdit.getValue();
        super.resize(minecraft, width, height);
    }
}
