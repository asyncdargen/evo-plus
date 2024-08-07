package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.math.Vector3
import java.awt.Color

fun MatrixStack.drawRectangle(size: Vector3, zLevel: Float = 0f, color: Color = Color.white) =
    drawRectangle(0f, 0f, size.x.toFloat(), size.y.toFloat(), zLevel, color)

fun MatrixStack.drawRectangle(
    minX: Float, minY: Float,
    maxX: Float, maxY: Float,
    zLevel: Float = 0f,
    color: Color = Color.white
) {
    val positionMatrix = peek().positionMatrix
    val buffer = Tesselator.buffer

    val (r, g, b, a) = color.decomposeFloat()

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)

    buffer.vertex(positionMatrix, minX, minY, zLevel).next()
    buffer.vertex(positionMatrix, minX, maxY, zLevel).next()
    buffer.vertex(positionMatrix, maxX, maxY, zLevel).next()
    buffer.vertex(positionMatrix, maxX, minY, zLevel).next()

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}

fun MatrixStack.drawCubeOutline(size: Vector3, color: Color = Color.white) =
    drawCubeOutline(0f, 0f, 0f, size.x.toFloat(), size.y.toFloat(), size.z.toFloat(), color)

fun MatrixStack.drawCubeOutline(
    minX: Float, minY: Float, minZ: Float,
    maxX: Float, maxY: Float, maxZ: Float,
    color: Color = Color.white
) {
    val position = peek().positionMatrix
    val buffer = Tesselator.buffer

    val (r, g, b, a) = color.decomposeFloat()

    buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION)

    // Bottom edges
    buffer.vertex(position, minX, minY, minZ).next()
    buffer.vertex(position, maxX, minY, minZ).next()

    buffer.vertex(position, maxX, minY, minZ).next()
    buffer.vertex(position, maxX, minY, maxZ).next()

    buffer.vertex(position, maxX, minY, maxZ).next()
    buffer.vertex(position, minX, minY, maxZ).next()

    buffer.vertex(position, minX, minY, maxZ).next()
    buffer.vertex(position, minX, minY, minZ).next()

    // Top edges
    buffer.vertex(position, minX, maxY, minZ).next()
    buffer.vertex(position, maxX, maxY, minZ).next()

    buffer.vertex(position, maxX, maxY, minZ).next()
    buffer.vertex(position, maxX, maxY, maxZ).next()

    buffer.vertex(position, maxX, maxY, maxZ).next()
    buffer.vertex(position, minX, maxY, maxZ).next()

    buffer.vertex(position, minX, maxY, maxZ).next()
    buffer.vertex(position, minX, maxY, minZ).next()

    // Vertical edges
    buffer.vertex(position, minX, minY, minZ).next()
    buffer.vertex(position, minX, maxY, minZ).next()

    buffer.vertex(position, maxX, minY, minZ).next()
    buffer.vertex(position, maxX, maxY, minZ).next()

    buffer.vertex(position, maxX, minY, maxZ).next()
    buffer.vertex(position, maxX, maxY, maxZ).next()

    buffer.vertex(position, minX, minY, maxZ).next()
    buffer.vertex(position, minX, maxY, maxZ).next()

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}