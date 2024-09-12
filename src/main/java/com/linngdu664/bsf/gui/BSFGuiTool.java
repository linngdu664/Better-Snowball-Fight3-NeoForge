package com.linngdu664.bsf.gui;

import com.linngdu664.bsf.Main;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.function.Consumer;

public class BSFGuiTool {
    public static final GuiTexture SNOWBALL_FRAME = new GuiTexture("textures/gui/snowball_frame.png", 23, 62);
    public static final GuiTexture TWEAKER_FRAME = new GuiTexture("textures/gui/tweaker_frame.png", 114, 106);
    public static final GuiImage SNOWBALL_SLOT_FRAME_GUI = new GuiImage(SNOWBALL_FRAME, 0, 0, 23, 62);
    public static final GuiImage TWEAKER_LOCATOR_GUI = new GuiImage(TWEAKER_FRAME, 1, 0, 22, 82);
    public static final GuiImage TWEAKER_STATUS_GUI = new GuiImage(TWEAKER_FRAME, 24, 0, 22, 102);
    public static final GuiImage TWEAKER_SELECTOR_GUI = new GuiImage(TWEAKER_FRAME, 0, 82, 24, 24);
    public static final GuiImage GOLEM_LOCATOR_GUI = new GuiImage(TWEAKER_FRAME, 47, 0, 22, 82);
    public static final GuiImage GOLEM_STATUS_GUI = new GuiImage(TWEAKER_FRAME, 70, 0, 22, 102);
    public static final GuiImage GOLEM_SELECTOR_GUI = new GuiImage(TWEAKER_FRAME, 46, 82, 24, 24);
    public static final GuiImage SETTER_ARROW_GUI = new GuiImage(TWEAKER_FRAME, 92, 1, 8, 20);
    public static final GuiImage ADVANCE_MODE_GUI = new GuiImage(TWEAKER_FRAME, 92, 60, 22, 22);
    public static final GuiImage EQUIPMENT_SLOT_FRAME_GUI = new GuiImage(TWEAKER_FRAME, 92, 84, 22, 22);


    public static class GuiTexture {
        public ResourceLocation texture;
        public int holeWidth;
        public int holeHeight;

        public GuiTexture(String path, int holeWidth, int holeHeight) {
            this.texture = Main.makeResLoc(path);
            this.holeWidth = holeWidth;
            this.holeHeight = holeHeight;
        }
    }

    public static class GuiImage {
        public GuiTexture guiTexture;
        public int widthOffset;
        public int heightOffset;
        public int width;
        public int height;

        public GuiImage(GuiTexture texture, int widthOffset, int heightOffset, int width, int height) {
            this.guiTexture = texture;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;
            this.width = width;
            this.height = height;
        }

        public V2I render(GuiGraphics guiGraphics, int x, int y) {
            RenderSystem.enableBlend();
            guiGraphics.blit(guiTexture.texture, x, y, widthOffset, heightOffset, width, height, guiTexture.holeWidth, guiTexture.holeHeight);
            RenderSystem.disableBlend();
            return new V2I(x, y);
        }

        public V2I renderCenterVertically(GuiGraphics guiGraphics, Window window, int x) {
            return render(guiGraphics, x, heightFrameCenter(window, this.height));
        }

        public V2I renderCenterHorizontally(GuiGraphics guiGraphics, Window window, int y) {
            return render(guiGraphics, widthFrameCenter(window, this.width), y);
        }

        public V2I renderRatio(GuiGraphics guiGraphics, Window window, double widthRatio, double heightRatio) {
            return renderRatio(guiGraphics, window, widthRatio, heightRatio, 0, 0);
        }

        public V2I renderRatio(GuiGraphics guiGraphics, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
            return render(guiGraphics, widthFrameRatio(window, this.width, widthRatio) + xOffset, heightFrameRatio(window, this.height, heightRatio) + yOffset);
        }
    }
    /*
        x width horizontal
        y height vertical
     */

    public static int heightFrameCenter(Window window, int height) {
        return heightFrameRatio(window, height, 0.5);
    }

    public static int heightFrameRatio(Window window, int height, double heightRatio) {
        return (int) ((window.getHeight() / window.getGuiScale() - height) * heightRatio);
    }

    public static int heightWinRatio(Window window, double heightRatio) {
        return heightFrameRatio(window, 0, heightRatio);
    }

    public static int widthFrameCenter(Window window, int width) {
        return widthFrameRatio(window, width, 0.5);
    }

    public static int widthFrameRatio(Window window, int width, double widthRatio) {
        return (int) ((window.getWidth() / window.getGuiScale() - width) * widthRatio);
    }

    public static int widthWinRatio(Window window, double widthRatio) {
        return widthFrameRatio(window, 0, widthRatio);
    }

    public static V2I v2IRatio(Window window, int width, int height, double widthRatio, double heightRatio) {
        return v2IRatio(window, width, height, widthRatio, heightRatio, 0, 0);
    }

    public static V2I v2IRatio(Window window, int width, int height, double widthRatio, double heightRatio, int xOffset, int yOffset) {
        return new V2I(widthFrameRatio(window, width, widthRatio) + xOffset, heightFrameRatio(window, height, heightRatio) + yOffset);
    }

    public static V2I v2IRatio(Window window, double widthRatio, double heightRatio) {
        return new V2I((int) (window.getWidth() * widthRatio / window.getGuiScale()), (int) (window.getHeight() * heightRatio / window.getGuiScale()));
    }

    public static class V2I {
        public int x;
        public int y;

        public V2I(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vec2 getVec2() {
            return new Vec2(this.x, this.y);
        }

        @Override
        public String toString() {
            return "V2I{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    /**
     * 渲染进度条
     *
     * @param guiGraphics
     * @param pos         进度条位置(取点左上角)
     * @param frame       进度条长宽
     * @param padding     内外框间隔
     * @param frameColor  外框颜色
     * @param innerColor  内框颜色
     * @param percent     进度条进度(0-1)
     */
    public static void renderProgressBar(GuiGraphics guiGraphics, V2I pos, V2I frame, int padding, int frameColor, int innerColor, float percent) {
        guiGraphics.fill(pos.x + 1, pos.y + 1, pos.x + frame.x - 1, pos.y + frame.y - 1, 0x80000000);
        guiGraphics.renderOutline(pos.x, pos.y, frame.x, frame.y, frameColor);
        int innerW = (int) ((frame.x - padding - padding) * percent);
        guiGraphics.fill(pos.x + padding, pos.y + padding, pos.x + padding + innerW, pos.y + frame.y - padding, innerColor);
    }

    /**
     * 渲染装备介绍
     *
     * @param guiGraphics
     * @param equipPoint    装备映射到屏幕上的位置
     * @param framePoint    装备框显示位置
     * @param lineXDistance 斜线水平长度
     * @param color         线颜色
     * @param itemStack     装备
     * @param font          字体
     * @param msg           装备描述
     */
    public static void renderEquipIntroduced(GuiGraphics guiGraphics, Vec2 equipPoint, Vec2 framePoint, int lineXDistance, int color, ItemStack itemStack, Font font, String msg) {
        Vec2 linkPoint = new Vec2(framePoint.x + EQUIPMENT_SLOT_FRAME_GUI.width, framePoint.y + (float) EQUIPMENT_SLOT_FRAME_GUI.height / 2);
        Vec2 xPoint = new Vec2(equipPoint.x - lineXDistance, linkPoint.y);
        if (xPoint.x < linkPoint.x) {
            xPoint = linkPoint;
        }
        float d = 2;
        renderLineTool(guiGraphics, xPoint.add(new Vec2(0, xPoint.y < equipPoint.y ? 0 : d)), equipPoint, d, color, xPoint.y < equipPoint.y, 0.3f, 0xff000000);
        renderLineTool(guiGraphics, linkPoint, xPoint, d, color, true, 0.3f, 0xff000000);
        renderFillSquareTool(guiGraphics, equipPoint.add(new Vec2(-2f, -1f)), equipPoint.add(new Vec2(2f, 3f)), 0xff000000);
        renderFillSquareTool(guiGraphics, equipPoint.add(new Vec2(-1f, 0)), equipPoint.add(new Vec2(1f, 2f)), color);
        EQUIPMENT_SLOT_FRAME_GUI.render(guiGraphics, (int) framePoint.x, (int) framePoint.y);
        guiGraphics.renderItem(itemStack, (int) (framePoint.x + 3), (int) (framePoint.y + 3));
        guiGraphics.drawString(font, msg, framePoint.x - font.width(msg), framePoint.y + 7, color, true);
    }

    public static void renderLineTool(GuiGraphics guiGraphics, Vec2 p1, Vec2 p2, float d, int color, boolean isDown, float padding, int padColor) {
        Vec2 ad = p2.add(p1.negated());
        Vec2 v1 = ad.scale(d / ad.length());
        Vec2 v2 = new Vec2(-v1.y, v1.x);
        if (isDown) {
//            Vec2 v2s = v2.scale(padding);
//            Vec2 v2d = v2.scale(1 - padding);
//
//            renderFillTool(guiGraphics, p1, p1.add(v2), p2.add(v2), p2, padColor);
//            renderFillTool(guiGraphics, p1.add(v2s), p1.add(v2d), p2.add(v2d), p2.add(v2s), color);

            Vec2 v2s = v2.scale(padding);

            renderFillTool(guiGraphics, p1, p1.add(v2), p2.add(v2), p2, padColor);
            renderFillTool(guiGraphics, p1, p1.add(v2s), p2.add(v2s), p2, color);
        } else {
            v2 = v2.negated();
//            Vec2 v2s = v2.scale(padding);
//            Vec2 v2d = v2.scale(1 - padding);
//            p2=p2.add(v2.negated());
//            renderFillTool(guiGraphics, p1.add(v2), p1, p2, p2.add(v2), padColor);
//            renderFillTool(guiGraphics, p1.add(v2d), p1.add(v2s), p2.add(v2s), p2.add(v2d), color);
            p2 = p2.add(v2.negated());
            Vec2 v2s = v2.scale(1 - padding);
            renderFillTool(guiGraphics, p1.add(v2), p1, p2, p2.add(v2), color);
            renderFillTool(guiGraphics, p1.add(v2s), p1, p2, p2.add(v2s), padColor);
        }

    }

    public static void renderFillTool(GuiGraphics guiGraphics, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int pColor) {
        Matrix4f matrix4f = guiGraphics.pose.last().pose();

        VertexConsumer vertexconsumer = guiGraphics.bufferSource.getBuffer(RenderType.gui());
        vertexconsumer.addVertex(matrix4f, a.x, a.y, 0).setColor(pColor);
        vertexconsumer.addVertex(matrix4f, b.x, b.y, 0).setColor(pColor);
        vertexconsumer.addVertex(matrix4f, c.x, c.y, 0).setColor(pColor);
        vertexconsumer.addVertex(matrix4f, d.x, d.y, 0).setColor(pColor);
        guiGraphics.flushIfUnmanaged();
    }

    public static void renderFillSquareTool(GuiGraphics guiGraphics, Vec2 a, Vec2 b, int pColor) {
        renderFillTool(guiGraphics, a, new Vec2(a.x, b.y), b, new Vec2(b.x, a.y), pColor);
    }

    public static void calcScreenPosFromWorldPos(List<Pair<Vec3, Consumer<Vec2>>> points, int guiWidth, int guiHeight, int widthProtect, int heightProtect, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        GameRenderer gameRenderer = mc.gameRenderer;
        Camera camera = gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Matrix3f rotMat = new Matrix3f().rotation(camera.rotation().conjugate(new Quaternionf()));      // make rot mat
        Window window = mc.getWindow();
        float fovy = (float) gameRenderer.getFov(camera, partialTicks, true) * Mth.DEG_TO_RAD;
        float tanHalfFovy = Mth.sin(fovy * 0.5F) / Mth.cos(fovy * 0.5F);
        float tanHalfFovx = tanHalfFovy * (float) window.getWidth() / (float) window.getHeight();
        for (Pair<Vec3, Consumer<Vec2>> pMethod : points) {
            doCalcScreenPos(pMethod, guiWidth, guiHeight, widthProtect, heightProtect, cameraPos, rotMat, tanHalfFovy, tanHalfFovx);
        }
    }

    public static void calcScreenPosFromWorldPos(Pair<Vec3, Consumer<Vec2>> point, int guiWidth, int guiHeight, int widthProtect, int heightProtect, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        GameRenderer gameRenderer = mc.gameRenderer;
        Camera camera = gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Matrix3f rotMat = new Matrix3f().rotation(camera.rotation().conjugate(new Quaternionf()));      // make rot mat
        Window window = mc.getWindow();
        float fovy = (float) gameRenderer.getFov(camera, partialTicks, true) * Mth.DEG_TO_RAD;
        float tanHalfFovy = Mth.sin(fovy * 0.5F) / Mth.cos(fovy * 0.5F);
        float tanHalfFovx = tanHalfFovy * (float) window.getWidth() / (float) window.getHeight();

        doCalcScreenPos(point, guiWidth, guiHeight, widthProtect, heightProtect, cameraPos, rotMat, tanHalfFovy, tanHalfFovx);
    }

    private static void doCalcScreenPos(Pair<Vec3, Consumer<Vec2>> point, int guiWidth, int guiHeight, int widthProtect, int heightProtect, Vec3 cameraPos, Matrix3f rotMat, float tanHalfFovy, float tanHalfFovx) {
        Vec3 vec3 = point.getA();
        Vector3f vector3f = new Vector3f((float) (vec3.x - cameraPos.x), (float) (vec3.y - cameraPos.y), (float) (vec3.z - cameraPos.z));
        rotMat.transform(vector3f);
        float rx = vector3f.x / -vector3f.z / tanHalfFovx;
        float xScreen = vector3f.z >= 0 ? (vector3f.x >= 0 ? guiWidth - widthProtect : widthProtect) : Mth.clamp(guiWidth * 0.5F * (1 + rx), widthProtect, guiWidth - widthProtect);
        float ry = vector3f.y / Mth.sqrt(vector3f.x * vector3f.x + vector3f.z * vector3f.z) / tanHalfFovy;
        float yScreen = Mth.clamp(guiHeight * 0.5F * (1 - ry), heightProtect, guiHeight - heightProtect);
        point.getB().accept(new Vec2(xScreen, yScreen));
    }
}