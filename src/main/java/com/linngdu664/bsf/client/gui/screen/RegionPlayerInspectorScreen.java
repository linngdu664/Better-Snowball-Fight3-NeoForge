package com.linngdu664.bsf.client.gui.screen;

import com.linngdu664.bsf.block.entity.RegionPlayerInspectorBlockEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.network.to_server.UpdateRegionPlayerInspectorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public class RegionPlayerInspectorScreen extends Screen {
    private static final int MAIN_MAX_WIDTH = 420;
    private static final int MAIN_MARGIN = 9;
    private static final int MAIN_HEIGHT = 10 + 15 + 4 * 25;  // 1閺傚洦婀?闂傛挳娈?4鐞涘矁绶崗銉︻攱
    private static final int LABEL_INPUT_GAP = 4;
    private final BlockPos blockPos;
    private final Component regionComponent;
    private final Component kickPosComponent = Component.translatable("gui.bsf.kick_pos");
    private final Component permittedTeamsComponent = Component.translatable("gui.bsf.permitted_teams");
    private final Component clearDirectlyItemsComponent = Component.translatable("gui.bsf.clear_directly");
    private final Component checkItemComponent = Component.translatable("gui.bsf.check_item");
    private final Component checkTeamComponent = Component.translatable("gui.bsf.check_team");
    private EditBox kickPosEdit;
    private EditBox permittedTeamsEdit;
    private EditBox clearDirectlyItemsEdit;
    private Checkbox checkItemCheckbox;
    private Checkbox checkTeamCheckbox;
    private String kickPosStr;
    private String permittedTeamsStr;
    private String clearDirectlyItemsStr;
    private boolean checkItem;
    private boolean checkTeam;

    public RegionPlayerInspectorScreen(BlockPos blockPos, RegionData region, BlockPos kickPos, short permittedTeams, List<String> clearDirectlyItems, boolean checkItem, boolean checkTeam) {
        super(Component.literal(""));
        this.blockPos = blockPos;
        BlockPos start = region.start();
        BlockPos end = region.end();
        this.regionComponent = Component.translatable("gui.bsf.battle_region_tip", start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
        this.kickPosStr = String.format("%d %d %d", kickPos.getX(), kickPos.getY(), kickPos.getZ());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if ((permittedTeams & (1 << i)) != 0) {
                sb.append(i);
                sb.append(' ');
            }
        }
        this.permittedTeamsStr = sb.toString();
        sb = new StringBuilder();
        for (String str : clearDirectlyItems) {
            sb.append(str);
            sb.append(' ');
        }
        this.clearDirectlyItemsStr = sb.toString();
        this.checkItem = checkItem;
        this.checkTeam = checkTeam;
    }

    // 鐠佸墽鐤嗙紒鍕
    @Override
    protected void init() {
        super.init();
        Font font = minecraft.font;
        // Layout
        int beginY = height / 2 - MAIN_HEIGHT / 2;
        int mainWidth = Math.min(MAIN_MAX_WIDTH, width - 2 * MAIN_MARGIN);
        int maxComponentWidth = Math.max(Math.max(font.width(kickPosComponent), font.width(permittedTeamsComponent)), font.width(clearDirectlyItemsComponent));
        int labelBeginX = width / 2 - mainWidth / 2;
        int inputBeginX = labelBeginX + maxComponentWidth + LABEL_INPUT_GAP;
        int inputWidth = width / 2 + mainWidth / 2 - inputBeginX;
        int leftCheckboxWidth = 21 + font.width(checkItemComponent);
        int leftCheckboxBeginX = width / 2 - mainWidth / 4 - leftCheckboxWidth / 2;
        int rightCheckboxWidth = 21 + font.width(checkTeamComponent);
        int rightCheckboxBeginX = width / 2 + mainWidth / 4 - rightCheckboxWidth / 2;


        StringWidget regionLabel = addRenderableWidget(new StringWidget(0, beginY, width, 9, regionComponent, font));
        ScreenAlignment.alignCenter(regionLabel, 0, width);

        checkItemCheckbox = addRenderableWidget(Checkbox.builder(checkItemComponent, font).pos(leftCheckboxBeginX, beginY + 25).selected(checkItem).build());
        checkTeamCheckbox = addRenderableWidget(Checkbox.builder(checkTeamComponent, font).pos(rightCheckboxBeginX, beginY + 25).selected(checkTeam).build());

        StringWidget kickPosLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 56, maxComponentWidth, 9, kickPosComponent, font));
        StringWidget permittedTeamsLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 81, maxComponentWidth, 9, permittedTeamsComponent, font));
        StringWidget clearDirectlyItemsLabel = addRenderableWidget(new StringWidget(labelBeginX, beginY + 106, maxComponentWidth, 9, clearDirectlyItemsComponent, font));
        ScreenAlignment.alignRight(kickPosLabel, labelBeginX, maxComponentWidth);
        ScreenAlignment.alignRight(permittedTeamsLabel, labelBeginX, maxComponentWidth);
        ScreenAlignment.alignRight(clearDirectlyItemsLabel, labelBeginX, maxComponentWidth);

        kickPosEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY + 50, inputWidth, 20, kickPosComponent));
        permittedTeamsEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY + 75, inputWidth, 20, permittedTeamsComponent));
        clearDirectlyItemsEdit = addRenderableWidget(new EditBox(font, inputBeginX, beginY + 100, inputWidth, 20, clearDirectlyItemsComponent));
        permittedTeamsEdit.setMaxLength(48);
        clearDirectlyItemsEdit.setMaxLength(256);
        kickPosEdit.setValue(kickPosStr);
        permittedTeamsEdit.setValue(permittedTeamsStr);
        clearDirectlyItemsEdit.setValue(clearDirectlyItemsStr);
    }

    @Override
    public void resize(int width, int height) {
        kickPosStr = kickPosEdit.getValue();
        permittedTeamsStr = permittedTeamsEdit.getValue();
        clearDirectlyItemsStr = clearDirectlyItemsEdit.getValue();
        checkItem = checkItemCheckbox.selected();
        checkTeam = checkTeamCheckbox.selected();
        super.resize(width, height);
    }

    // 閸忔娊妫碐UI閿涘苯鎮滈張宥呭閸ｃ劌褰傞柅浣稿棘閺?    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.level.getBlockEntity(blockPos) instanceof RegionPlayerInspectorBlockEntity) {
            // Send packet
            try {
                String[] strs = kickPosEdit.getValue().split("\\s+");
                if (strs.length <= 2) {
                    return;
                }
                BlockPos blockPos1 = new BlockPos(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
                String str1 = permittedTeamsEdit.getValue();
                int permittedTeams1 = 0;
                if (!str1.isEmpty()) {
                    for (String str : str1.split("\\s+")) {
                        permittedTeams1 |= (1 << Integer.parseInt(str));
                    }
                }
                ClientPacketDistributor.sendToServer(new UpdateRegionPlayerInspectorPayload(blockPos, blockPos1, (short) permittedTeams1, List.of(clearDirectlyItemsEdit.getValue().split("\\s+")), checkItemCheckbox.selected(), checkTeamCheckbox.selected()));
            } catch (NumberFormatException ignore) {
            }
        }
    }
}

