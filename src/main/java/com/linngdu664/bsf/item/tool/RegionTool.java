package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

public class RegionTool extends Item {
    public RegionTool(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        Vec3 pos = context.getClickedPos().getCenter();
        RegionData region = itemInHand.getOrDefault(DataComponentRegister.REGION, new RegionData(null, null));
        RegionData regionData;
        if (player != null && player.isShiftKeyDown()) {
            regionData = new RegionData(pos, region.end());
            itemInHand.set(DataComponentRegister.REGION, regionData);
        } else {
            regionData = new RegionData(region.start(), pos);
            itemInHand.set(DataComponentRegister.REGION, regionData);
        }
        if (player != null) {
            player.displayClientMessage(MutableComponent.create(new PlainTextContents.LiteralContents(regionData.toString())), false);
        }
        return super.useOn(context);
    }
}
