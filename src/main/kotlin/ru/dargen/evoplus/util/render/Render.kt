package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.ColorHelper
import ru.dargen.evoplus.util.Client

object Render {

    val TextRenderer get() = Client.textRenderer
    val ItemRenderer get() = Client.itemRenderer

    fun fill(matrices: MatrixStack, x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
        val matrix4f = matrices.peek().positionMatrix

        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        var i: Double
        
        if (x1 < x2) {
            i = x1
            x1 = x2
            x2 = i
        }
        
        if (y1 < y2) {
            i = y1
            y1 = y2
            y2 = i
        }
        
        val a = ColorHelper.Argb.getAlpha(color).toFloat() / 255.0f
        val r = ColorHelper.Argb.getRed(color).toFloat() / 255.0f
        val g = ColorHelper.Argb.getGreen(color).toFloat() / 255.0f
        val b = ColorHelper.Argb.getBlue(color).toFloat() / 255.0f
        
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0f).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0f).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0f).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0f).color(r, g, b, a).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.disableBlend()
    }

}