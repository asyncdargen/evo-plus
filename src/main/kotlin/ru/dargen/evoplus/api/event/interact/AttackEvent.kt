package ru.dargen.evoplus.api.event.interact

import net.minecraft.entity.Entity
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.api.event.Event

class AttackEvent(val entity: Entity) : CancellableEvent()