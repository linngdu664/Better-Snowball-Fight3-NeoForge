package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowGolemModeTweakerItem extends Item {
    public SnowGolemModeTweakerItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1)
                .component(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0)
                .component(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0)
        );
    }

    public static String locatorMap(byte lc) {
        return switch (lc) {
            case 0 -> "snow_golem_locator_monster.tip";
            case 1 -> "snow_golem_locator_specify.tip";
            case 2 -> "snow_golem_locator_enemy_team.tip";
            default -> "snow_golem_locator_all_creatures.tip";
        };
    }

    public static String statusMap(byte st) {
        return switch (st) {
            case 0 -> "snow_golem_standby.tip";
            case 1 -> "snow_golem_follow.tip";
            case 2 -> "snow_golem_follow_and_attack.tip";
            case 3 -> "snow_golem_attack.tip";
            default -> "snow_golem_turret.tip";
        };
    }

//    @Override
//    public void onCraftedBy(ItemStack pStack, @NotNull Level pLevel, @NotNull Player pPlayer) {
//        pStack.getOrCreateTag().putByte("Status", (byte) 0);
//        pStack.getOrCreateTag().putByte("Locator", (byte) 0);
//    }


    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        Options options = Minecraft.getInstance().options;
        new BSFCommonUtil.TipBuilder(pTooltipComponents)
                .add("snow_golem_mode_tweaker.tooltip", ChatFormatting.DARK_GRAY, options.keyShift.getTranslatedKeyMessage())
                .add("snow_golem_mode_tweaker1.tooltip", ChatFormatting.DARK_GRAY, options.keySprint.getTranslatedKeyMessage())
                .add("snow_golem_mode_tweaker2.tooltip", ChatFormatting.BLUE)
                .add("tweaker_target.tip", ChatFormatting.DARK_GRAY, BSFCommonUtil.getTransStr(locatorMap(stack.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0))))
                .add("tweaker_status.tip", ChatFormatting.DARK_GRAY, BSFCommonUtil.getTransStr(statusMap(stack.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0))));
    }
}
