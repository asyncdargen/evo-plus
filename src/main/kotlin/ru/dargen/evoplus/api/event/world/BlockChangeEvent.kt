package ru.dargen.evoplus.api.event.world

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.Event

class BlockChangeEvent(val pos: BlockPos, val oldState: BlockState?, val newState: BlockState) : Event