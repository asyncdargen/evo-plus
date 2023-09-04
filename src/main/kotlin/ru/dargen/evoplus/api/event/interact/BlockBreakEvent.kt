package ru.dargen.evoplus.api.event.interact

import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.Event

class BlockBreakEvent(val blockPos: BlockPos) : Event