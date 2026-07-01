package com.linngdu664.bsf.client.renderer.entity;

import com.linngdu664.bsf.client.model.BlackHoleExecutorCModel;
import com.linngdu664.bsf.client.renderer.entity.state.BlackHoleExecutorRenderState;
import com.linngdu664.bsf.entity.executor.BlackHoleExecutor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class BlackHoleExecutorCRenderer extends EntityRenderer<BlackHoleExecutor, BlackHoleExecutorRenderState> {
    private final BlackHoleExecutorCModel model;
    private final RenderType renderType = RenderTypes.entityCutout(BlackHoleExecutorCModel.LAYER_LOCATION.model());

    public BlackHoleExecutorCRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.1F;
        ModelPart modelpart = pContext.bakeLayer(BlackHoleExecutorCModel.LAYER_LOCATION);
        model = new BlackHoleExecutorCModel(modelpart);
    }

    @Override
    public BlackHoleExecutorRenderState createRenderState() {
        return new BlackHoleExecutorRenderState();
    }

    @Override
    public void extractRenderState(BlackHoleExecutor entity, BlackHoleExecutorRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.modelTicker = entity.getModelTicker();
        state.timer = entity.getTimer();
        state.rank = entity.getRank();
        state.angle1 = entity.getAngle1();
        state.axis = entity.getAxis();
        state.obliquity = entity.getObliquity();
        state.projection = entity.getProjection();
    }

    @Override
    public void submit(BlackHoleExecutorRenderState state, PoseStack pPoseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        pPoseStack.pushPose();
        float growingSize = Math.min(state.modelTicker + state.partialTick, (float) Math.sqrt(state.rank) * 1.7f);

        pPoseStack.scale(growingSize, growingSize, growingSize);

        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(state.angle1, state.axis)));

//        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(30 * Mth.DEG_TO_RAD, BSFMthUtil.SIN_30, 0F, BSFMthUtil.SIN_60)));
        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(state.obliquity, state.projection)));
        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(((state.timer + state.partialTick) * BlackHoleExecutor.SPINNING_SPEED) % 360 * Mth.DEG_TO_RAD, 0, 1, 0)));

        submitNodeCollector.submitModelPart(model.getPlate(), pPoseStack, renderType, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
        pPoseStack.scale(0.6f, 0.6f, 0.6f);
//        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(((pEntity.getTimer() + pPartialTick) * 10) % 360 * Mth.DEG_TO_RAD, SIN_30, 0F, SIN_60)));
        submitNodeCollector.submitModelPart(model.getBody(), pPoseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null);


        pPoseStack.popPose();
        super.submit(state, pPoseStack, submitNodeCollector, camera);
    }

}
