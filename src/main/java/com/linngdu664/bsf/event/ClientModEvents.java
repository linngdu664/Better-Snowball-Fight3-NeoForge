package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.model.*;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.tool.ColdCompressionJetEngineItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping CYCLE_MOVE_AMMO_NEXT = new KeyMapping("key.bsf.ammo_switch_next", GLFW.GLFW_KEY_H, "key.categories.misc");
    public static final KeyMapping CYCLE_MOVE_AMMO_PREV = new KeyMapping("key.bsf.ammo_switch_prev", GLFW.GLFW_KEY_G, "key.categories.misc");

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(CYCLE_MOVE_AMMO_NEXT);
        event.register(CYCLE_MOVE_AMMO_PREV);
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegister.COLD_COMPRESSION_JET_ENGINE.get(),
                    ResourceLocation.withDefaultNamespace("sc_xxx"), (itemStack, world, livingEntity, num) -> ((float) itemStack.getMaxDamage() - itemStack.getDamageValue()) / itemStack.getMaxDamage());
            ItemProperties.register(ItemRegister.COLD_COMPRESSION_JET_ENGINE.get(),
                    ResourceLocation.withDefaultNamespace("sc_starting"), (itemStack, world, livingEntity, num) -> {
                        if (livingEntity == null || livingEntity.getUseItem() != itemStack) {
                            return 0.0F;
                        } else {
                            float pct = (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / ColdCompressionJetEngineItem.STARTUP_DURATION;
                            return pct > 1.4f ? 2.0f : pct;
                        }
                    });
            ItemProperties.register(ItemRegister.IMPLOSION_SNOWBALL_CANNON.get(),
                    ResourceLocation.withDefaultNamespace("cooling"), (itemStack, world, livingEntity, num) -> {
                        if (livingEntity instanceof Player player) {
                            return player.getCooldowns().getCooldownPercent(itemStack.getItem(), 1);
                        } else {
                            return 0;
                        }
                    });
            ItemProperties.register(ItemRegister.SNOWBALL_CANNON.get(),
                    ResourceLocation.withDefaultNamespace("pull"), (itemStack, world, livingEntity, num) -> {
                        if (livingEntity == null) {
                            return 0.0F;
                        } else {
                            return livingEntity.getUseItem() != itemStack ? 0.0F : (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                        }
                    });
            ItemProperties.register(ItemRegister.SNOWBALL_CANNON.get(), ResourceLocation.withDefaultNamespace("pulling"), (itemStack, world, livingEntity, num)
                    -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.FREEZING_SNOWBALL_CANNON.get(),
                    ResourceLocation.withDefaultNamespace("pull"), (itemStack, world, livingEntity, num) -> {
                        if (livingEntity == null) {
                            return 0.0F;
                        } else {
                            return livingEntity.getUseItem() != itemStack ? 0.0F : (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                        }
                    });
            ItemProperties.register(ItemRegister.FREEZING_SNOWBALL_CANNON.get(), ResourceLocation.withDefaultNamespace("pulling"), (itemStack, world, livingEntity, num)
                    -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.POWERFUL_SNOWBALL_CANNON.get(),
                    ResourceLocation.withDefaultNamespace("pull"), (itemStack, world, livingEntity, num) -> {
                        if (livingEntity == null) {
                            return 0.0F;
                        } else {
                            return livingEntity.getUseItem() != itemStack ? 0.0F : (float) (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                        }
                    });
            ItemProperties.register(ItemRegister.POWERFUL_SNOWBALL_CANNON.get(), ResourceLocation.withDefaultNamespace("pulling"), (itemStack, world, livingEntity, num)
                    -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.GLOVE.get(), ResourceLocation.withDefaultNamespace("using"), (itemStack, world, livingEntity, num)
                    -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.JEDI_GLOVE.get(), ResourceLocation.withDefaultNamespace("using"), (itemStack, world, livingEntity, num)
                    -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.LARGE_SNOWBALL_TANK.get(), ResourceLocation.withDefaultNamespace("snowball"), (itemStack, world, livingEntity, num) -> {
                Item item = itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
                if (item instanceof AbstractBSFSnowballItem snowballItem) {
                    return snowballItem.getIdForTank();
                }
                return -1;
            });
            ItemProperties.register(ItemRegister.SNOWBALL_TANK.get(), ResourceLocation.withDefaultNamespace("snowball"), (itemStack, world, livingEntity, num) -> {
                Item item = itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
                if (item instanceof AbstractBSFSnowballItem snowballItem) {
                    return snowballItem.getIdForTank();
                }
                return -1;
            });
            ItemProperties.register(ItemRegister.SNOW_GOLEM_CONTAINER.get(), ResourceLocation.withDefaultNamespace("has_golem"), (itemStack, world, livingEntity, num) -> itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA) ? 1.0F : 0.0F);
            ItemProperties.register(ItemRegister.BASIN.get(), ResourceLocation.withDefaultNamespace("snow_type"), (itemStack, world, livingEntity, num) -> (itemStack.getOrDefault(DataComponentRegister.BASIN_SNOW_TYPE, (byte) 0)));
        });
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
