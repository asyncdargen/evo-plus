package ru.dargen.evoplus.api.event.entity

import net.minecraft.entity.Entity
import ru.dargen.evoplus.api.event.CancellableEvent

class EntitySpawnEvent(val entity: Entity) : CancellableEvent()