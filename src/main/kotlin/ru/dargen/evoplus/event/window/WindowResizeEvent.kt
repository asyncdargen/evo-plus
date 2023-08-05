package ru.dargen.evoplus.event.window

import ru.dargen.evoplus.event.Event

data class WindowResizeEvent(val width: Double, val height: Double) : Event