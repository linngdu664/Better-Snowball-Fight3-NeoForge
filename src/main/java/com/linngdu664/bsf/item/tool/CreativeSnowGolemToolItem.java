package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreativeSnowGolemToolItem extends Item {
    public CreativeSnowGolemToolItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!level.isClientSide) {
            ItemStack stack = pContext.getItemInHand();
            if (stack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(level);
                snowGolem.readAdditionalSaveData(stack.get(DataComponentRegister.SNOW_GOLEM_DATA));
                BlockPos blockPos = pContext.getClickedPos();
                snowGolem.moveTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0.0F, 0.0F);
                level.addFreshEntity(snowGolem);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("creative_snow_golem_tool.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("creative_snow_golem_tool1.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("creative_snow_golem_tool2.tooltip", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("creative_snow_golem_tool3.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
