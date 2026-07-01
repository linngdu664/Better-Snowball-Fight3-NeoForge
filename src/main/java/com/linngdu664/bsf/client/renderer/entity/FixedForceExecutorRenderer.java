package com.linngdu664.bsf.client.renderer.entity;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.model.FixedForceExecutorModel;
import com.linngdu664.bsf.client.renderer.entity.state.FixedForceExecutorRenderState;
import com.linngdu664.bsf.entity.executor.AbstractFixedForceExecutor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class FixedForceExecutorRenderer extends EntityRenderer<AbstractFixedForceExecutor, FixedForceExecutorRenderState> {
    private static final float SIN_45 = (float) Math.sin(Math.PI / 4D);
    private final FixedForceExecutorLayerType layerType;
    private final FixedForceExecutorModel model;
    private final RenderType renderType;

    public FixedForceExecutorRenderer(EntityRendererProvider.Context pContext, FixedForceExecutorLayerType layerType) {
        super(pContext);
        this.layerType = layerType;
        this.shadowRadius = 0.1F;
        this.renderType = RenderTypes.entityCutout(getTexture());
        ModelPart modelpart = pContext.bakeLayer(new ModelLayerLocation(getTexture(), "main"));
        model = new FixedForceExecutorModel(modelpart);
    }

    @Override
    public FixedForceExecutorRenderState createRenderState() {
        return new FixedForceExecutorRenderState();
    }

    @Override
    public void extractRenderState(AbstractFixedForceExecutor entity, FixedForceExecutorRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.modelTicker = entity.getModelTicker();
    }

    @Override
    public void submit(FixedForceExecutorRenderState state, PoseStack pPoseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        pPoseStack.scale(1F, 1F, 1F);
        pPoseStack.pushPose();


        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(((state.modelTicker + state.partialTick) * 10) % 360 * Mth.DEG_TO_RAD, SIN_45, 0F, SIN_45)));
        submitNodeCollector.submitModelPart(model.getCircle1(), pPoseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(((state.modelTicker + state.partialTick) * 20) % 360 * Mth.DEG_TO_RAD, -SIN_45, 0F, SIN_45)));
        submitNodeCollector.submitModelPart(model.getCircle2(), pPoseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        pPoseStack.mulPose(new Quaternionf(new AxisAngle4f(((state.modelTicker + state.partialTick) * 30) % 360 * Mth.DEG_TO_RAD, SIN_45, 0F, -SIN_45)));
        submitNodeCollector.submitModelPart(model.getBb_main(), pPoseStack, renderType, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);

        pPoseStack.popPose();
        super.submit(state, pPoseStack, submitNodeCollector, camera);
    }

    private Identifier getTexture() {
        switch (layerType) {
            case MONSTER_GRAVITY -> {
                return Main.makeResLoc("textures/models/monster_gravity_executor.png");
            }
            case MONSTER_REPULSION -> {
                return Main.makeResLoc("textures/models/monster_repulsion_executor.png");
            }
            case PROJECTILE_GRAVITY -> {
                return Main.makeResLoc("textures/models/projectile_gravity_executor.png");
            }
            default -> {
                return Main.makeResLoc("textures/models/projectile_repulsion_executor.png");
            }
        }
    }
}
