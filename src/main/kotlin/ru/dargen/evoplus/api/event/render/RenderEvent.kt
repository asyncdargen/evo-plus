package ru.dargen.evoplus.api.event.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.event.Event
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RenderEvent(val matrices: MatrixStack, val tickDelta: Float) : Event

@KotlinOpens
class WorldRenderEvent(matrices: MatrixStack, tickDelta: Float) : RenderEvent(matrices, tickDelta)
