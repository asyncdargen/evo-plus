package ru.dargen.evoplus.api.event.world.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.api.event.CancellableEvent

class BlockEntityLoadEvent(val chunk: WorldChunk, val blockEntity: BlockEntity) : CancellableEvent()