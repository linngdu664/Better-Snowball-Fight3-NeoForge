package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SnowGolemModeTweakerItem extends Item {
    public SnowGolemModeTweakerItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1)
                .component(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0)
                .component(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0)
        );
    }

//    @Override
//    public void onCraftedBy(ItemStack pStack, @NotNull Level pLevel, @NotNull Player pPlayer) {
//        pStack.getOrCreateTag().putByte("Status", (byte) 0);
//        pStack.getOrCreateTag().putByte("Locator", (byte) 0);
//    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Options options = Minecraft.getInstance().options;
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snow_golem_mode_tweaker.tooltip", null, new Object[]{options.keyShift.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snow_golem_mode_tweaker1.tooltip", null, new Object[]{options.keySprint.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snow_golem_mode_tweaker2.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        int targetMode = stack.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
        int status = stack.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
        tooltipComponents.add(MutableComponent.create(new TranslatableContents(switch (targetMode) {
            case 0 -> "snow_golem_locator_monster.tip";
            case 1 -> "snow_golem_locator_specify.tip";
            case 2 -> "snow_golem_locator_enemy_team.tip";
            default -> "snow_golem_locator_all_creatures.tip";
        }, null, new Object[0])).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents(switch (status) {
            case 0 -> "snow_golem_standby.tip";
            case 1 -> "snow_golem_follow.tip";
            case 2 -> "snow_golem_follow_and_attack.tip";
            case 3 -> "snow_golem_attack.tip";
            default -> "snow_golem_turret.tip";
        }, null, new Object[0])).withStyle(ChatFormatting.DARK_GRAY));
    }
}
