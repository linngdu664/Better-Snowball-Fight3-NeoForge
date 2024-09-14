package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.item.component.IntegerGroupData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ValueAdjustmentTool extends Item {
    public ValueAdjustmentTool(Properties properties) {
        super(properties.stacksTo(1).component(DataComponentRegister.SELECTED_INDEX.get(), 0).component(DataComponentRegister.INTEGER_GROUP.get(), IntegerGroupData.EMPTY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            int index = itemStack.getOrDefault(DataComponentRegister.SELECTED_INDEX.get(), 0);
            IntegerGroupData group = itemStack.getOrDefault(DataComponentRegister.INTEGER_GROUP.get(), IntegerGroupData.EMPTY);
            if (index == 4) {
                index = 0;
            } else {
                index++;
            }
            switch (index) {
                case 0: itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val1: " + group.val1()))); break;
                case 1: itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val2: " + group.val2()))); break;
                case 2: itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val3: " + group.val3()))); break;
                case 3: itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val4: " + group.val4()))); break;
                default: itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new PlainTextContents.LiteralContents("val5: " + group.val5()))); break;
            }
            itemStack.set(DataComponentRegister.SELECTED_INDEX.get(), index);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        IntegerGroupData group = stack.getOrDefault(DataComponentRegister.INTEGER_GROUP.get(), IntegerGroupData.EMPTY);
        tooltipComponents.add(Component.literal("val1: " + group.val1()));
        tooltipComponents.add(Component.literal("val2: " + group.val2()));
        tooltipComponents.add(Component.literal("val3: " + group.val3()));
        tooltipComponents.add(Component.literal("val4: " + group.val4()));
        tooltipComponents.add(Component.literal("val5: " + group.val5()));
    }
}
