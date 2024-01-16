package ru.dargen.evoplus.api.event.entity

import net.minecraft.entity.Entity
import ru.dargen.evoplus.api.event.Event

data class EntityRemoveEvent(val entity: Entity) : Event