package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.model.*;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping CYCLE_MOVE_AMMO_NEXT = new KeyMapping("key.bsf.ammo_switch_next", GLFW.GLFW_KEY_H, KeyMapping.Category.MISC);
    public static final KeyMapping CYCLE_MOVE_AMMO_PREV = new KeyMapping("key.bsf.ammo_switch_prev", GLFW.GLFW_KEY_G, KeyMapping.Category.MISC);

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(CYCLE_MOVE_AMMO_NEXT);
        event.register(CYCLE_MOVE_AMMO_PREV);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions weaponExtensions = new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return HumanoidModel.ArmPose.valueOf("BSF_WEAPON");
            }
        };
        event.registerItem(
                weaponExtensions,
                ItemRegister.IMPLOSION_SNOWBALL_CANNON.get(),
                ItemRegister.SNOWBALL_SHOTGUN.get(),
                ItemRegister.SCULK_SNOWBALL_LAUNCHER.get(),
                ItemRegister.TARGET_LOCATOR.get()
        );
    }

    // current max id for tank is 31 (icicle snowball)

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(IceSkatesModel.LAYER_LOCATION, IceSkatesModel::createBodyLayer);
        event.registerLayerDefinition(SnowFallBootsModel.LAYER_LOCATION, SnowFallBootsModel::createBodyLayer);
        event.registerLayerDefinition(BSFSnowGolemModel.LAYER_LOCATION, BSFSnowGolemModel::createBodyLayer);
        event.registerLayerDefinition(FixedForceExecutorModel.LAYER_LOCATION1, FixedForceExecutorModel::createBodyLayer);
        event.registerLayerDefinition(FixedForceExecutorModel.LAYER_LOCATION2, FixedForceExecutorModel::createBodyLayer);
        event.registerLayerDefinition(FixedForceExecutorModel.LAYER_LOCATION3, FixedForceExecutorModel::createBodyLayer);
        event.registerLayerDefinition(FixedForceExecutorModel.LAYER_LOCATION4, FixedForceExecutorModel::createBodyLayer);
        event.registerLayerDefinition(BlackHoleExecutorCModel.LAYER_LOCATION, BlackHoleExecutorCModel::createBodyLayer);
    }
}
