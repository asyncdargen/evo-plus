package ru.dargen.evoplus.api.event.world

import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.api.event.Event

class ChunkLoadEvent(val chunk: WorldChunk) : Event

class ChunkUnloadEvent(val chunk: WorldChunk) : Event