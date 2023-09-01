package ru.dargen.evoplus.api.event.game

import ru.dargen.evoplus.api.event.Event

class InteractEvent(
    val isRightClick: Boolean
) : Event {
}