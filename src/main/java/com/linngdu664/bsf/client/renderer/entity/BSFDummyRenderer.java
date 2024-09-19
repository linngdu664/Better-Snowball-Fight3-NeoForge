package com.linngdu664.bsf.client.renderer.entity;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.model.BSFDummyModel;
import com.linngdu664.bsf.client.model.BSFSnowGolemModel;
import com.linngdu664.bsf.entity.BSFDummyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BSFDummyRenderer extends MobRenderer<BSFDummyEntity, BSFDummyModel<BSFDummyEntity>> {
    public BSFDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new BSFDummyModel<>(context.bakeLayer(BSFSnowGolemModel.LAYER_LOCATION)), 0.7f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BSFDummyEntity pEntity) {
        switch (pEntity.getStyle()) {
            case 0 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_1.png");
            }
            case 1 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_2.png");
            }
            case 2 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_3.png");
            }
            case 3 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_4.png");
            }
            case 4 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_5.png");
            }
            case 5 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_6.png");
            }
            case 6 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_7.png");
            }
            case 7 -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_8.png");
            }
            default -> {
                return Main.makeResLoc("textures/models/bsf_snow_golem_9.png");
            }
        }
    }
}
