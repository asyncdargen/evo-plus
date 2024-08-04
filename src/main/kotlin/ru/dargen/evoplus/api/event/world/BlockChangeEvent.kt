package ru.dargen.evoplus.api.event.world

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.event.Event

class BlockChangeEvent(val chunk: Chunk, val blockPos: BlockPos, val oldState: BlockState?, val newState: BlockState) : Event