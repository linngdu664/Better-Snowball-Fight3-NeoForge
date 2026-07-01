package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CreativeSnowGolemToolItem extends Item {
    public CreativeSnowGolemToolItem() {
        super(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            ItemStack stack = pContext.getItemInHand();
            if (stack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
                CompoundTag tag = stack.get(DataComponentRegister.SNOW_GOLEM_DATA);
                if (snowGolem != null && tag != null) {
                    snowGolem.load(TagValueInput.create(ProblemReporter.DISCARDING, serverLevel.registryAccess(), tag));
                    BlockPos blockPos = pContext.getClickedPos();
                    snowGolem.absSnapTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0.0F, 0.0F);
                    level.addFreshEntity(snowGolem);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("creative_snow_golem_tool.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("creative_snow_golem_tool1.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("creative_snow_golem_tool2.tooltip", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("creative_snow_golem_tool3.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

