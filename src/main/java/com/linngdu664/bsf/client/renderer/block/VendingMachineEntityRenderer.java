package com.linngdu664.bsf.client.renderer.block;

import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.event.ClientForgeEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class VendingMachineEntityRenderer implements BlockEntityRenderer<VendingMachineBlockEntity> {
    public VendingMachineEntityRenderer(BlockEntityRendererProvider.Context context) {
        // No-op
    }

    @Override
    public void render(VendingMachineBlockEntity vendingMachineBlockEntity, float partialTick, PoseStack transform, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack goods = vendingMachineBlockEntity.getGoods();
        if (!goods.isEmpty()) {
            float rotation = ClientForgeEvents.tickCount + partialTick;
            transform.pushPose();
            transform.translate(0.5, 1.2, 0.5);
            transform.scale(0.625F, 0.625F, 0.625F);
            transform.mulPose(Axis.YP.rotationDegrees(rotation));
            Minecraft.getInstance().getItemRenderer().renderStatic(goods, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, packedOverlay, transform, bufferSource, vendingMachineBlockEntity.getLevel(), (int) vendingMachineBlockEntity.getBlockPos().asLong());
            transform.popPose();
        }
    }
}
