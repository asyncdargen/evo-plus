package ru.dargen.evoplus.event.input

import ru.dargen.evoplus.event.Event
import ru.dargen.evoplus.util.math.Vector3

data class MouseWheelEvent(val mouse: Vector3, val hWheel: Double, val vWheel: Double) : Event

data class MouseClickEvent(val mouse: Vector3, val button: Int, val state: Boolean) : Event

data class MouseMoveEvent(val mouse: Vector3) : Event