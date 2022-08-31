package ru.dargen.evoplus.util.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.dargen.evoplus.util.Util;

import java.util.function.BiConsumer;

@UtilityClass
public class Render {

    public void scaledRunner(float scale, BiConsumer<Integer, Integer> drawer) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        drawer.accept((int) (Util.getWidth() / scale), (int) (Util.getHeight() / scale));
        GL11.glPopMatrix();
    }

    //Rectangles
    public void drawHorizontalLine(MatrixStack matrices, int x, int y, int width, int color) {
        fill(matrices, x, y, width, 1, color);
    }

    public void drawVerticalLine(MatrixStack matrices, int x, int y, int height, int color) {
        fill(matrices, x, y, 1, height, color);
    }

    public void fill(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        fill0(matrixStack.peek().getModel(), x, y, x + width, y + height, color);
    }

    private void fill0(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        int j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void fillGradient(MatrixStack matrixStack, int x, int y, int width, int height, int colorStart, int colorEnd) {
        fillGradient0(matrixStack, x, y, x + width, y + height, colorStart, colorEnd);
    }

    private void fillGradient0(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient0(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, 0, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private void fillGradient0(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int z, int colorStart, int colorEnd) {
        float f = (float) (colorStart >> 24 & 255) / 255.0F;
        float g = (float) (colorStart >> 16 & 255) / 255.0F;
        float h = (float) (colorStart >> 8 & 255) / 255.0F;
        float i = (float) (colorStart & 255) / 255.0F;
        float j = (float) (colorEnd >> 24 & 255) / 255.0F;
        float k = (float) (colorEnd >> 16 & 255) / 255.0F;
        float l = (float) (colorEnd >> 8 & 255) / 255.0F;
        float m = (float) (colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrix, (float) xEnd, (float) yStart, (float) z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float) xStart, (float) yStart, (float) z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float) xStart, (float) yEnd, (float) z).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, (float) xEnd, (float) yEnd, (float) z).color(k, l, m, j).next();
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
        drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, 0, (float) u, (float) v, width, height, 256, 256);
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
        drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    private void drawTexture(MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(matrices.peek().getModel(), x0, y0, x1, y1, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight);
    }

    private void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    //Items

    public ItemRenderer getItemRenderer() {
        return Util.getClient().getItemRenderer();
    }

    public void drawItem(ItemStack itemStack, int x, int y) {
        getItemRenderer().renderGuiItemIcon(itemStack, x, y);
    }
}
