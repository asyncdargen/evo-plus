package ru.dargen.evoplus.api.event.inventory

import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.api.event.Event

class InventoryCloseEvent(val syncId: Int, val openEvent: InventoryOpenEvent?) : CancellableEvent()