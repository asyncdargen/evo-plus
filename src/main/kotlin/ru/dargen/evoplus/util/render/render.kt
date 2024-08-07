package ru.dargen.evoplus.util.render

import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Tessellator
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

val TextRenderer get() = Client.textRenderer
val ItemRenderer get() = Client.itemRenderer
val Tesselator = Tessellator.getInstance()

lateinit var MatrixStack: MatrixStack
lateinit var BufferBuilderStorage: BufferBuilderStorage
lateinit var Camera: Camera

val DefaultScale = v3(1.0, 1.0, 1.0)

val MatrixStack.positionMatrix get() = peek().positionMatrix

fun MatrixStack.translate(translate: Vector3, scale: Vector3 = DefaultScale, multi: Double = 1.0) {
    translate(translate.x * scale.x * multi, translate.y * scale.y * multi, translate.z * scale.z * multi)
}

fun MatrixStack.scale(scale: Vector3 = DefaultScale) = scale(scale.x.toFloat(), scale.y.toFloat(), scale.z.toFloat())

fun MatrixStack.rotate(rotation: Vector3) = rotate(rotation.y.toFloat(), rotation.x.toFloat(), rotation.z.toFloat())

fun MatrixStack.rotate(yaw: Float = 0f, pitch: Float = 0f, roll: Float = 0f) = with(positionMatrix) {
    rotate(yaw, 0f, 1f, 0f) //yaw y
    rotate(pitch, 1f, 0f, 0f) //pitch x
    rotate(roll, 0F, 0f, 1f) //roll z
}
