package com.linngdu664.bsf.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Vector3f;

public class BlackHoleExecutorRenderState extends EntityRenderState {
    public int modelTicker;
    public int timer;
    public int rank;
    public float angle1;
    public float obliquity;
    public Vector3f axis = new Vector3f(1, 0, 0);
    public Vector3f projection = new Vector3f(1, 0, 0);
}
