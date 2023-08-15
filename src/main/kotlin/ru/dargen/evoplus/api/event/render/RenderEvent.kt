package ru.dargen.evoplus.api.event.render

import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import ru.dargen.evoplus.api.event.Event
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RenderEvent(val matrices: MatrixStack, val tickDelta: Float) : Event

@KotlinOpens
class WorldRenderEvent(
    matrices: MatrixStack,
    tickDelta: Float,
    val camera: Camera,
    val positionMatrix: Matrix4f,
    val vertexConsumers: VertexConsumerProvider.Immediate,
) : RenderEvent(matrices, tickDelta)
