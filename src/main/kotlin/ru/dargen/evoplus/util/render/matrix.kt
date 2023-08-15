package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.ColorHelper
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

val TextRenderer get() = Client.textRenderer
val ItemRenderer get() = Client.itemRenderer

private val DefaultScale = v3(1.0, 1.0, 1.0)
private val ZeroPosition = v3()

fun MatrixStack.translate(translate: Vector3, scale: Vector3 = DefaultScale) {
    translate(translate.x * scale.x, translate.y * scale.y, translate.z * scale.z)
}

fun MatrixStack.scale(scale: Vector3 = DefaultScale) {
    scale(scale.x.toFloat(), scale.x.toFloat(), scale.z.toFloat())
}

fun MatrixStack.rotate(rotation: Vector3) = with(peek()) {
    positionMatrix.rotate(rotation.x.toFloat(), 1f, 0f, 0f)
    positionMatrix.rotate(rotation.y.toFloat(), 0f, 1f, 0f)
    positionMatrix.rotate(rotation.z.toFloat(), 0F, 0f, 1f)
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

    val a = ColorHelper.Argb.getAlpha(color).toFloat() / 255.0f
    val r = ColorHelper.Argb.getRed(color).toFloat() / 255.0f
    val g = ColorHelper.Argb.getGreen(color).toFloat() / 255.0f
    val b = ColorHelper.Argb.getBlue(color).toFloat() / 255.0f

    val bufferBuilder = Tessellator.getInstance().buffer
    RenderSystem.disableDepthTest()
    RenderSystem.enableBlend()
    RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
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
