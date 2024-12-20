package com.linngdu664.bsf.client.renderer.entity.layers;

import com.linngdu664.bsf.client.model.BSFSnowGolemModel;
import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class BSFSnowGolemHoldItemLayer extends RenderLayer<AbstractBSFSnowGolemEntity, BSFSnowGolemModel<AbstractBSFSnowGolemEntity>> {
    public BSFSnowGolemHoldItemLayer(RenderLayerParent<AbstractBSFSnowGolemEntity, BSFSnowGolemModel<AbstractBSFSnowGolemEntity>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, AbstractBSFSnowGolemEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack itemstack = pLivingEntity.getWeapon();
        ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        if (!itemstack.isEmpty()) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(new Quaternionf(new AxisAngle4f(pNetHeadYaw * Mth.DEG_TO_RAD * 0.25F, 0F, 1F, 0F)));
            pMatrixStack.translate(-0.05, 0.2, -0.8);
            pMatrixStack.mulPose(new Quaternionf(new AxisAngle4f(Math.max(pLivingEntity.getWeaponAng() - 60 * pPartialTicks, 0) * Mth.DEG_TO_RAD, 1F, 0F, 0F)));
            itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.HEAD, false, pMatrixStack, pBuffer, pPackedLight);
            pMatrixStack.popPose();
        }
        itemstack = pLivingEntity.getAmmo();
        if (!itemstack.isEmpty()) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(new Quaternionf(new AxisAngle4f(pNetHeadYaw * Mth.DEG_TO_RAD * 0.25F, 0F, 1F, 0F)));
            itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.HEAD, false, pMatrixStack, pBuffer, pPackedLight);
            pMatrixStack.popPose();
        }
        itemstack = pLivingEntity.getCore();
        if (!itemstack.isEmpty()) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(new Quaternionf(new AxisAngle4f(pNetHeadYaw * Mth.DEG_TO_RAD * 0.25F, 0F, 1F, 0F)));
            pMatrixStack.translate(0, 0.15, -0.48);
            pMatrixStack.scale(0.35F, 0.35F, 0.35F);
            itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.HEAD, false, pMatrixStack, pBuffer, pPackedLight);
            pMatrixStack.popPose();
        }
    }
}
