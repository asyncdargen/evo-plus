package ru.dargen.evoplus.api.event.world

import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.event.Event

class ChunkDataEvent(val chunk: Chunk) : Event