package com.linngdu664.bsf.client.gui;

import com.linngdu664.bsf.client.renderer.state.gui.FloatBlitRenderState;
import com.linngdu664.bsf.client.renderer.state.gui.FloatColoredQuadRenderState;
import com.linngdu664.bsf.client.renderer.state.gui.FloatColoredRectangleRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2f;

public class GuiUtil {
    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int color) {
        fill(guiGraphics, a.x, a.y, b.x, b.y, color);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, float x0, float y0, float x1, float y1, int color) {
        fill(guiGraphics, RenderPipelines.GUI, x0, y0, x1, y1, color);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, float x0, float y0, float x1, float y1, int color) {
        if (x0 < x1) {
            float tmp = x0;
            x0 = x1;
            x1 = tmp;
        }
        if (y0 < y1) {
            float tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredRectangleRenderState(renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, color, color, guiGraphics.scissorStack.peek()));
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int color) {
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredQuadRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), a, b, c, d, color, guiGraphics.scissorStack.peek()));
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, Identifier texture, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        blit(guiGraphics, RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier texture, float x, float y, float u, float v, float width, float height, float srcWidth, float srcHeight, float textureWidth, float textureHeight) {
        blit(guiGraphics, renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, -1);
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier texture, float x, float y, float u, float v, float width, float height, float srcWidth, float srcHeight, float textureWidth, float textureHeight, int color) {
        innerBlit(guiGraphics, renderPipeline, texture, x, x + width, y, y + height, u / textureWidth, (u + srcWidth) / textureWidth, v / textureHeight, (v + srcHeight) / textureHeight, color);
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color) {
        AbstractTexture texture = guiGraphics.minecraft.getTextureManager().getTexture(location);
        innerBlit(guiGraphics, renderPipeline, texture.getTextureView(), texture.getSampler(), x0, y0, x1, y1, u0, u1, v0, v1, color);
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
        guiGraphics.guiRenderState.addGuiElement(new FloatBlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, u0, u1, v0, v1, color, guiGraphics.scissorStack.peek()));
    }
}
