package com.linngdu664.bsf.client.renderer.entity.state;

import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;

public class BSFSnowGolemRenderState extends LivingEntityRenderState {
    public ItemStack weapon = ItemStack.EMPTY;
    public ItemStack ammo = ItemStack.EMPTY;
    public ItemStack core = ItemStack.EMPTY;
    public int weaponAngle;
    public byte style;
    public AbstractBSFSnowGolemEntity golem;
}
