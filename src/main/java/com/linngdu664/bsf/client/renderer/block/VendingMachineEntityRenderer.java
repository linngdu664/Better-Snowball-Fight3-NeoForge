package com.linngdu664.bsf.client.renderer.block;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
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

public class VendingMachineEntityRenderer implements BlockEntityRenderer<VendingMachineEntity> {
    public VendingMachineEntityRenderer(BlockEntityRendererProvider.Context context) {
        // No-op
    }
    @Override
    public void render(VendingMachineEntity vendingMachineEntity, float partialTick, PoseStack transform, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack goods = vendingMachineEntity.getGoods();
        if (!goods.isEmpty()) {
            float rotation = ClientForgeEvents.tickCount + partialTick;
            transform.pushPose();
            transform.translate(0.5, 2, 0.5);
            transform.scale(0.625F, 0.625F, 0.625F);
            transform.mulPose(Axis.YP.rotationDegrees(rotation));
            Minecraft.getInstance().getItemRenderer().renderStatic(goods, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, packedOverlay, transform, bufferSource, vendingMachineEntity.getLevel (), (int) vendingMachineEntity.getBlockPos().asLong());
            transform.popPose();
        }
    }
}
