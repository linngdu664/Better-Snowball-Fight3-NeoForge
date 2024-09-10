package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ScoringDevice extends Item {
    public ScoringDevice(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int rank = stack.getOrDefault(DataComponentRegister.RANK.get(), 0);
        int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
        tooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents("rank: "+ rank)));
        tooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents("money: "+ money)));
    }
}
