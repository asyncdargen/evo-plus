package ru.dargen.evoplus.features.esp

import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.world.cube
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.evo.isDiamondShardHead
import ru.dargen.evoplus.util.evo.isGoldenShardHead
import ru.dargen.evoplus.util.evo.isHead
import ru.dargen.evoplus.util.evo.isWallHead
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.forEachBlocks
import java.awt.Color

object ESPFeature : Feature("esp", "Подсветка", Items.SEA_LANTERN) {

    private val Shards = mutableMapOf<BlockPos, Node>()

    val ShardEsp by settings.boolean("Подсвечивание осколков", false) on { state ->
        Shards.values.forEach { it.enabled = state }
    }

    init {
        on<ChunkLoadEvent> {
            if (!ShardEsp) return@on

            chunk.forEachBlocks { blockPos, blockState ->
                val isGoldenShardHead = blockState.isGoldenShardHead(blockPos, chunk)

                if (!isGoldenShardHead && !blockState.isDiamondShardHead(blockPos, chunk)) return@forEachBlocks

                when {
                    blockState.isHead() -> Shards[blockPos.mutableCopy()] = blockPos.mutableCopy().renderShard(
                        if (isGoldenShardHead) Colors.Gold else Colors.Diamond
                    )
                    blockState.isWallHead() -> Shards[blockPos.mutableCopy()] =  blockPos.mutableCopy().renderWallShard(
                        if (isGoldenShardHead) Colors.Gold else Colors.Diamond
                    )
                }
            }
        }

        on<ChunkUnloadEvent> {
            if (!ShardEsp) return@on

            chunk.forEachBlocks { blockPos, blockState ->
                if (!blockState.isGoldenShardHead(blockPos, chunk) && !blockState.isDiamondShardHead(blockPos, chunk)) return@forEachBlocks

                World.removeChildren(Shards.remove(blockPos) ?: return@forEachBlocks)
            }
        }
    }

    private fun BlockPos.renderShard(color: Color) =
        World + cube {
            position = v3(x.toDouble() + .75, y.toDouble() + .5, z.toDouble() + 0.25)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }

    private fun BlockPos.renderWallShard(color: Color) =
        World + cube {
            position = v3(x.toDouble() + .3, y.toDouble() + .65, z.toDouble() + 0.2)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }
}