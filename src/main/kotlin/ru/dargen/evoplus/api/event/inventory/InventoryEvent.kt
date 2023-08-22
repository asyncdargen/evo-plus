package ru.dargen.evoplus.api.event.inventory

import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class InventoryEvent(val syncId: Int) : CancellableEvent()