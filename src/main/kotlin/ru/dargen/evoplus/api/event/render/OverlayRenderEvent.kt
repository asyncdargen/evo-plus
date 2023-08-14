package ru.dargen.evoplus.api.event.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class OverlayRenderEvent(matrices: MatrixStack, tickDelta: Float) : RenderEvent(matrices, tickDelta)