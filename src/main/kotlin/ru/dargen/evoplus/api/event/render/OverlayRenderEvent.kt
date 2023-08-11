package ru.dargen.evoplus.api.event.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.event.Event

data class OverlayRenderEvent(val matrices: MatrixStack, val tickDelta: Float) : Event