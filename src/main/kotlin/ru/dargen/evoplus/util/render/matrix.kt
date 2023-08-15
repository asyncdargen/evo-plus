package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

val TextRenderer get() = Client.textRenderer
val ItemRenderer get() = Client.itemRenderer
val Tesselator get() = RenderSystem.renderThreadTesselator()

val Vector3.toVec3d get() = Vec3d(x, y, z)

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

fun MatrixStack.drawWorldText(
    position: Vec3d,
    camera: Camera,
    vertexConsumers: VertexConsumerProvider.Immediate,
    texts: List<Text>,
    verticalAlign: VerticalAlign = VerticalAlign.CENTER
) {
    push()
    translate(position.x, position.y, position.z)
    multiply(camera.rotation)
    for ((index, text) in texts.withIndex()) {
        push()
        val width = TextRenderer.getWidth(text)
        translate(-width / 2F, verticalAlign.align(index, texts.size), 0F)
        val vertexConsumer: VertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough())
        val matrix4f = peek().positionMatrix
        vertexConsumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(0x70808080)
            .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
        vertexConsumer.vertex(matrix4f, -1.0f, TextRenderer.fontHeight.toFloat(), 0.0f).color(0x70808080)
            .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
        vertexConsumer.vertex(matrix4f, width.toFloat(), TextRenderer.fontHeight.toFloat(), 0.0f)
            .color(0x70808080)
            .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
        vertexConsumer.vertex(matrix4f, width.toFloat(), -1.0f, 0.0f).color(0x70808080)
            .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
        translate(0F, 0F, 0.01F)

        TextRenderer.draw(
            text,
            0F,
            0F,
            -1,
            false,
            peek().positionMatrix,
            vertexConsumers,
            net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )
        pop()
    }
    pop()
    vertexConsumers.drawCurrentLayer()
}

fun MatrixStack.drawWorldBlock(blockPos: BlockPos, color: Int) {
    push()
    translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
    peek().positionMatrix.buildCube(Tesselator.buffer, color)
    Tesselator.draw()
    pop()
}

fun MatrixStack.drawWorldSizedBlock(vec3d: Vec3d, size: Float, color: Int) {
    push()
    RenderSystem.setShader(GameRenderer::getPositionColorProgram)
    translate(vec3d.x, vec3d.y, vec3d.z)
    scale(size, size, size)
    translate(-.5, -.5, -.5)
    peek().positionMatrix.buildCube(Tesselator.buffer, color)
    Tesselator.draw()
    pop()
}

private fun Matrix4f.buildCube(buf: BufferBuilder, color: Int) {
    val a = ColorHelper.Argb.getAlpha(color).toFloat() / 255.0f
    val r = ColorHelper.Argb.getRed(color).toFloat() / 255.0f
    val g = ColorHelper.Argb.getGreen(color).toFloat() / 255.0f
    val b = ColorHelper.Argb.getBlue(color).toFloat() / 255.0f

    buf.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)
    buf.fixedColor(255, 255, 255, 255)
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 0.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 0.0F, 1.0F, 1.0F).color(r, g, b, a).next()
    buf.vertex(this, 1.0F, 0.0F, 1.0F).color(r, g, b, a).next()
    buf.unfixColor()
}

enum class VerticalAlign {
    TOP, BOTTOM, CENTER;

    fun align(index: Int, count: Int) = when (this) {
        CENTER -> (index - count / 2F) * (1 + TextRenderer.fontHeight.toFloat())
        BOTTOM -> (index - count) * (1 + TextRenderer.fontHeight.toFloat())
        TOP -> (index) * (1 + TextRenderer.fontHeight.toFloat())
    }
}