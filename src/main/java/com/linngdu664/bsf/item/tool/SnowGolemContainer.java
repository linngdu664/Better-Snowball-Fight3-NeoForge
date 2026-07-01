package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
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

public class SnowGolemContainer extends Item {
    public SnowGolemContainer() {
        super(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        if (itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
            if (level instanceof ServerLevel serverLevel) {
                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
                CompoundTag tag = itemStack.get(DataComponentRegister.SNOW_GOLEM_DATA);
                if (snowGolem == null || tag == null) {
                    return InteractionResult.FAIL;
                }
                snowGolem.load(TagValueInput.create(ProblemReporter.DISCARDING, serverLevel.registryAccess(), tag));
                BlockPos blockPos = pContext.getClickedPos();
                snowGolem.absSnapTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0.0F, 0.0F);
                snowGolem.setOwnerUUID(player.getUUID());
                level.addFreshEntity(snowGolem);
                itemStack.remove(DataComponentRegister.SNOW_GOLEM_DATA);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("snow_golem_container.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

