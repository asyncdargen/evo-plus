package ru.dargen.evoplus.util.minecraft

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.chunk.Chunk


fun Chunk.forEachBlocks(iter: (BlockState) -> Unit) {
    val blockPos = BlockPos.Mutable()
    for (x in pos.startX..pos.endX) {
        for (z in pos.startZ..pos.endZ) {
            val height: Int = getHeightmap(Heightmap.Type.WORLD_SURFACE)
                .get(x - pos.startX, z - pos.startZ)

            for (y in Client.world!!.bottomY until height) {
                blockPos.set(x, y, z)
                iter(getBlockState(blockPos))
            }
        }
    }
}