package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.renderer.item.properties.conditional.HasGolem;
import com.linngdu664.bsf.client.renderer.item.properties.numeric.Cooling;
import com.linngdu664.bsf.client.renderer.item.properties.numeric.ScStarting;
import com.linngdu664.bsf.client.renderer.item.properties.numeric.ScXXX;
import com.linngdu664.bsf.client.renderer.item.properties.numeric.SnowType;
import com.linngdu664.bsf.client.renderer.item.properties.numeric.Snowball;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class ItemModelPropertyRegistry {
    @SubscribeEvent
    public static void registerRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Main.makeResLoc("sc_xxx"), ScXXX.MAP_CODEC);
        event.register(Main.makeResLoc("sc_starting"), ScStarting.MAP_CODEC);
        event.register(Main.makeResLoc("cooling"), Cooling.MAP_CODEC);
        event.register(Main.makeResLoc("snowball"), Snowball.MAP_CODEC);
        event.register(Main.makeResLoc("snow_type"), SnowType.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerConditionalItemModelProperty(RegisterConditionalItemModelPropertyEvent event) {
        event.register(Main.makeResLoc("has_golem"), HasGolem.MAP_CODEC);
    }
}
