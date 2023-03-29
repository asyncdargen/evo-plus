package ru.dargen.evoplus.util.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import ru.dargen.evoplus.util.Util;

import java.util.function.BiConsumer;

@UtilityClass
public class Render {

    public void scaledRunner(MatrixStack matrixStack, float scale, BiConsumer<Integer, Integer> drawer) {
        matrixStack.push();
        matrixStack.scale(scale, scale, scale);
        drawer.accept((int) (Util.getWidth() / scale), (int) (Util.getHeight() / scale));
        matrixStack.pop();
    }

    //Rectangles
    public void drawHorizontalLine(MatrixStack matrices, int x, int y, int width, int color) {
        fill(matrices, x, y, width, 1, color);
    }

    public void drawVerticalLine(MatrixStack matrices, int x, int y, int height, int color) {
        fill(matrices, x, y, 1, height, color);
    }

    public void fill(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        fill0(matrixStack, x, y, x + width, y + height, color);
    }

    private void fill0(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float f = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float j = (float) ColorHelper.Argb.getBlue(color) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, 0).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, 0).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, 0).color(g, h, j, f).next();
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, 0).color(g, h, j, f).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public void fillGradient(MatrixStack matrixStack, int x, int y, int width, int height, int colorStart, int colorEnd) {
        fillGradient0(matrixStack, x, y, x + width, y + height, colorStart, colorEnd, 0);
    }

    private void fillGradient0(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient0(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    private void fillGradient0(Matrix4f matrix, BufferBuilder builder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = (float) ColorHelper.Argb.getAlpha(colorStart) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(colorStart) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(colorStart) / 255.0F;
        float i = (float) ColorHelper.Argb.getBlue(colorStart) / 255.0F;
        float j = (float) ColorHelper.Argb.getAlpha(colorEnd) / 255.0F;
        float k = (float) ColorHelper.Argb.getRed(colorEnd) / 255.0F;
        float l = (float) ColorHelper.Argb.getGreen(colorEnd) / 255.0F;
        float m = (float) ColorHelper.Argb.getBlue(colorEnd) / 255.0F;
        builder.vertex(matrix, (float)startX, (float)startY, (float)z).color(g, h, i, f).next();
        builder.vertex(matrix, (float)startX, (float)endY, (float)z).color(k, l, m, j).next();
        builder.vertex(matrix, (float)endX, (float)endY, (float)z).color(k, l, m, j).next();
        builder.vertex(matrix, (float)endX, (float)startY, (float)z).color(g, h, i, f).next();
    }

    //String

    public TextRenderer getTextRenderer() {
        return Util.getClient().textRenderer;
    }

    public int getStringWidth(String string) {
        return getTextRenderer().getWidth(string);
    }

    public int getStringHeight() {
        return getTextRenderer().fontHeight;
    }

    public void drawCenteredStringWithShadow(MatrixStack matrixStack, String text, int centerX, int y, int color) {
        getTextRenderer().drawWithShadow(matrixStack, text, (float) (centerX - getStringWidth(text) / 2), (float) y, color);
    }

    public void drawCenteredString(MatrixStack matrixStack, String text, int centerX, int y, int color) {
        getTextRenderer().draw(matrixStack, text, (float) (centerX - getStringWidth(text) / 2), (float) y, color);
    }

    public void drawStringWithShadow(MatrixStack matrices, String text, int x, int y, int color) {
        getTextRenderer().drawWithShadow(matrices, text, (float) x, (float) y, color);
    }

    public void drawString(MatrixStack matrices, String text, int x, int y, int color) {
        getTextRenderer().draw(matrices, text, (float) x, (float) y, color);
    }

    //Textures
    public void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, Sprite sprite) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, 0, (float)u, (float)v, width, height, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    private static void drawTexture(MatrixStack matrices, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }

    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
        bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    //Items

    public ItemRenderer getItemRenderer() {
        return Util.getClient().getItemRenderer();
    }

    public void drawItem(MatrixStack matrixStack, ItemStack itemStack, int x, int y) {
        getItemRenderer().renderGuiItemIcon(matrixStack, itemStack, x, y);
    }
}
