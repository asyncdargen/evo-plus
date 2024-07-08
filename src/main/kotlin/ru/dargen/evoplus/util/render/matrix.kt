package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

val TextRenderer get() = Client.textRenderer
val ItemRenderer get() = Client.itemRenderer
val Tesselator = Tessellator.getInstance()
lateinit var MatrixStack: MatrixStack

val DefaultScale = v3(1.0, 1.0, 1.0)
val ZeroPosition = v3()

val MatrixStack.positionMatrix get() = peek().positionMatrix

fun MatrixStack.translate(translate: Vector3, scale: Vector3 = DefaultScale, mult: Double = 1.0) {
    translate(translate.x * scale.x * mult, translate.y * scale.y * mult, translate.z * scale.z * mult)
}

fun MatrixStack.scale(scale: Vector3 = DefaultScale) {
    scale(scale.x.toFloat(), scale.x.toFloat(), scale.z.toFloat())
}

fun MatrixStack.rotate(rotation: Vector3) = with(peek()) {
    positionMatrix.rotate(rotation.y.toFloat(), 0f, 1f, 0f) //yaw
    positionMatrix.rotate(rotation.x.toFloat(), 1f, 0f, 0f) //pitch
    positionMatrix.rotate(rotation.z.toFloat(), 0F, 0f, 1f) //roll
}

fun MatrixStack.fill(v1: Vector3 = ZeroPosition, v2: Vector3, color: Int) =
    fill(v1.x, v1.y, v2.x, v2.y, color)

fun MatrixStack.fill(x1: Double, y1: Double, x2: Double, y2: Double, color: Int) {
    val matrix4f = peek().positionMatrix

    var x1 = x1
    var y1 = y1
    var x2 = x2
    var y2 = y2
    var tmp: Double

    if (x1 < x2) {
        tmp = x1
        x1 = x2
        x2 = tmp
    }

    if (y1 < y2) {
        tmp = y1
        y1 = y2
        y2 = tmp
    }

    val (r, g, b, a) = color.decomposeColorFloat()

    val bufferBuilder = Tesselator.buffer
    RenderSystem.enableBlend()
    RenderSystem.setShader(GameRenderer::getPositionColorProgram)

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), 0f).color(r, g, b, a).next()
    bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), 0f).color(r, g, b, a).next()
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), 0f).color(r, g, b, a).next()
    bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), 0f).color(r, g, b, a).next()

    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    RenderSystem.disableBlend()
}

fun MatrixStack.drawText(text: String, position: Vector3 = ZeroPosition, shadow: Boolean, color: Int) {
    if (shadow) drawTextWithShadow(text, position, color)
    else drawText(text, position, color)
}

fun MatrixStack.drawText(text: String, position: Vector3 = ZeroPosition, color: Int) {
    TextRenderer.draw(this, text, position.x.toFloat(), position.y.toFloat(), color)
}

fun MatrixStack.drawTextWithShadow(text: String, position: Vector3 = ZeroPosition, color: Int) {
    TextRenderer.drawWithShadow(this, text, position.x.toFloat(), position.y.toFloat(), color)
}

fun MatrixStack.drawCube(size: Vector3, color: Int) {
    val (r, g, b, a) = color.decomposeColorFloat()

    val x = size.x.toFloat()
    val y = size.y.toFloat()
    val z = size.z.toFloat()

    val position = peek().positionMatrix
    val tesselator = Tesselator
    val buffer = tesselator.buffer

    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionColorProgram)
    push()
    buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR)

    // Bottom edges
    buffer.vertex(position, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, x, 0.0F, 0.0F).color(r, g, b, a).next()

    buffer.vertex(position, x, 0.0F, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, x, 0.0F, z).color(r, g, b, a).next()

    buffer.vertex(position, x, 0.0F, z).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, 0.0F, z).color(r, g, b, a).next()

    buffer.vertex(position, 0.0F, 0.0F, z).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()

    // Top edges
    buffer.vertex(position, 0.0F, y, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, x, y, 0.0F).color(r, g, b, a).next()

    buffer.vertex(position, x, y, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, x, y, z).color(r, g, b, a).next()

    buffer.vertex(position, x, y, z).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, y, z).color(r, g, b, a).next()

    buffer.vertex(position, 0.0F, y, z).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, y, 0.0F).color(r, g, b, a).next()

    // Vertical edges
    buffer.vertex(position, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, y, 0.0F).color(r, g, b, a).next()

    buffer.vertex(position, x, 0.0F, 0.0F).color(r, g, b, a).next()
    buffer.vertex(position, x, y, 0.0F).color(r, g, b, a).next()

    buffer.vertex(position, x, 0.0F, z).color(r, g, b, a).next()
    buffer.vertex(position, x, y, z).color(r, g, b, a).next()

    buffer.vertex(position, 0.0F, 0.0F, z).color(r, g, b, a).next()
    buffer.vertex(position, 0.0F, y, z).color(r, g, b, a).next()

    tesselator.draw()
    pop()
}

