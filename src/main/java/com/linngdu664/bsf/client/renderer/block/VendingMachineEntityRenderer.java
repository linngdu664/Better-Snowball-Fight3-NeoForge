package com.linngdu664.bsf.client.renderer.block;

import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.event.ClientForgeEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class VendingMachineEntityRenderer implements BlockEntityRenderer<VendingMachineBlockEntity, VendingMachineEntityRenderer.State> {
    private final ItemModelResolver itemModelResolver;

    public VendingMachineEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VendingMachineBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.goods = blockEntity.getGoods().copy();
        state.rotation = ClientForgeEvents.tickCount + partialTicks;
        state.seed = (int) blockEntity.getBlockPos().asLong();
    }

    @Override
    public void submit(State state, PoseStack transform, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.goods.isEmpty()) {
            return;
        }
        ItemStackRenderState itemState = new ItemStackRenderState();
        itemModelResolver.updateForTopItem(itemState, state.goods, ItemDisplayContext.GROUND, null, null, state.seed);
        transform.pushPose();
        transform.translate(0.5, 1.2, 0.5);
        transform.scale(0.625F, 0.625F, 0.625F);
        transform.mulPose(Axis.YP.rotationDegrees(state.rotation));
        itemState.submit(transform, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        transform.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public ItemStack goods = ItemStack.EMPTY;
        public float rotation;
        public int seed;
    }
}
