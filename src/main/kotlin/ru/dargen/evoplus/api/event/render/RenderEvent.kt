package ru.dargen.evoplus.api.event.render

import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RenderEvent(val matrices: MatrixStack, val tickDelta: Float) : CancellableEvent()

@KotlinOpens
class WorldRenderEvent(
    matrices: MatrixStack,
    tickDelta: Float,
    val camera: Camera,
    val vertexConsumers: VertexConsumerProvider.Immediate,
) : RenderEvent(matrices, tickDelta)
