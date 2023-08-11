package ru.dargen.evoplus.util.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

private val DefaultScale = 1.v3
private val ZeroPosition = 1.v3

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
    Render.fill(this, v1.x, v1.y, v2.x, v2.y, color)

fun MatrixStack.drawText(text: String, position: Vector3 = ZeroPosition, shadow: Boolean, color: Int) {
    if (shadow) drawTextWithShadow(text, position, color)
    else drawTextWithShadow(text, position, color)
}

fun MatrixStack.drawText(text: String, position: Vector3 = ZeroPosition, color: Int) {
    Render.TextRenderer.draw(this, text, position.x.toFloat(), position.y.toFloat(), color)
}

fun MatrixStack.drawTextWithShadow(text: String, position: Vector3 = ZeroPosition, color: Int) {
    Render.TextRenderer.drawWithShadow(this, text, position.x.toFloat(), position.y.toFloat(), color)
}
