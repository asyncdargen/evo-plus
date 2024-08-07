package ru.dargen.evoplus.util.render

import net.minecraft.client.render.Tessellator
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

