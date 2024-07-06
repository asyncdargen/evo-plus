package ru.dargen.evoplus.api.event.world

import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.event.Event

class ChunkLoadEvent(val chunk: Chunk) : Event

class ChunkUnloadEvent(val chunk: Chunk) : Event