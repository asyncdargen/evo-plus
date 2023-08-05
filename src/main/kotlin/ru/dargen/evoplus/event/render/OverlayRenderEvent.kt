package ru.dargen.evoplus.event.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.Event

data class OverlayRenderEvent(val matrices: MatrixStack, val tickDelta: Float) : Event