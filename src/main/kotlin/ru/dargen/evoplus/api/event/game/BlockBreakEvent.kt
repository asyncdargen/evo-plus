package ru.dargen.evoplus.api.event.game

import net.minecraft.block.Material
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.Event

class BlockBreakEvent(val blockPos: BlockPos) : Event