package ru.dargen.evoplus.api.event.world

import net.minecraft.world.World
import ru.dargen.evoplus.api.event.Event

class WorldPreLoadEvent(val world: World) : Event
class WorldPostLoadEvent(val world: World) : Event