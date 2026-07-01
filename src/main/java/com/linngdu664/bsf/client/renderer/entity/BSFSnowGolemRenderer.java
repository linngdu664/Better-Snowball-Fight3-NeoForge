package com.linngdu664.bsf.client.renderer.entity;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.model.BSFSnowGolemModel;
import com.linngdu664.bsf.client.renderer.entity.layers.BSFSnowGolemHoldItemLayer;
import com.linngdu664.bsf.client.renderer.entity.state.BSFSnowGolemRenderState;
import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class BSFSnowGolemRenderer extends MobRenderer<AbstractBSFSnowGolemEntity, BSFSnowGolemRenderState, BSFSnowGolemModel> {
    public BSFSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new BSFSnowGolemModel(context.bakeLayer(BSFSnowGolemModel.LAYER_LOCATION)), 0.7f);
        this.addLayer(new BSFSnowGolemHoldItemLayer(this));
    }

    @Override
    public BSFSnowGolemRenderState createRenderState() {
        return new BSFSnowGolemRenderState();
    }

    @Override
    public void extractRenderState(AbstractBSFSnowGolemEntity entity, BSFSnowGolemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.weapon = entity.getWeapon();
        state.ammo = entity.getAmmo();
        state.core = entity.getCore();
        state.weaponAngle = entity.getWeaponAng();
        state.style = entity.getStyle();
        state.golem = entity;
    }

    @Override
    public Identifier getTextureLocation(BSFSnowGolemRenderState state) {
        return switch (state.style) {
            case 0 -> Main.makeResLoc("textures/models/bsf_snow_golem_1.png");
            case 1 -> Main.makeResLoc("textures/models/bsf_snow_golem_2.png");
            case 2 -> Main.makeResLoc("textures/models/bsf_snow_golem_3.png");
            case 3 -> Main.makeResLoc("textures/models/bsf_snow_golem_4.png");
            case 4 -> Main.makeResLoc("textures/models/bsf_snow_golem_5.png");
            case 5 -> Main.makeResLoc("textures/models/bsf_snow_golem_6.png");
            case 6 -> Main.makeResLoc("textures/models/bsf_snow_golem_7.png");
            case 7 -> Main.makeResLoc("textures/models/bsf_snow_golem_8.png");
            default -> Main.makeResLoc("textures/models/bsf_snow_golem_9.png");
        };
    }
}
