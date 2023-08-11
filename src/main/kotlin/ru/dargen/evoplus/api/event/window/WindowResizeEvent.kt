package ru.dargen.evoplus.api.event.window

import ru.dargen.evoplus.api.event.Event

data class WindowResizeEvent(val width: Double, val height: Double) : Event