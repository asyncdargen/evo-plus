package ru.dargen.evoplus.features.esp

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.api.render.node.world.cube
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.evo.isHead
import ru.dargen.evoplus.util.evo.isWallHead
import ru.dargen.evoplus.util.evo.renderShard
import ru.dargen.evoplus.util.evo.renderWallShard
import ru.dargen.evoplus.util.minecraft.forEachBlocks
import ru.dargen.evoplus.util.minecraft.printMessage

object ESPFeature : Feature("esp", "Подсветка", Items.SEA_LANTERN) {

    const val goldShardHead =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ=="
    const val diamondShardHead =
        "ewogICJ0aW1lc3RhbXAiIDogMTU5ODUyMjU3OTk4MiwKICAicHJvZmlsZUlkIiA6ICJmMDk3N2NmZWZlZmY0ZGM1OGUyMGIzOTVlMjBiYWJkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkaWFtb25kZHVkZTMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE3NjdmYWEzNjZjODA1Nzc5NTJmNWUwMDc4MTU5ZDU5NzdmMzcyMDJmMzhkNDgxN2Q0YTkyNDVhZDQ4YTkwZCIKICAgIH0KICB9Cn0="

    val shards = mutableMapOf<BlockPos, Node>()

    val ShardEsp by settings.boolean("Подсвечивание осколков", false)

    init {

        on<ChunkLoadEvent> {
            if (!ShardEsp) return@on

            chunk.forEachBlocks { blockPos, blockState ->
                if (!blockState.isHead() && !blockState.isWallHead()) return@forEachBlocks

                val owner = chunk.getBlockEntity(blockPos, BlockEntityType.SKULL)?.get()?.owner ?: return@forEachBlocks
                val property = owner.properties.asMap()["textures"]?.firstOrNull() ?: return@forEachBlocks
                val value = property.value

                if (value != goldShardHead && value != diamondShardHead) return@forEachBlocks

                printMessage(blockPos.toString())
                when {
                    blockState.isHead() -> shards[blockPos] = blockPos.mutableCopy().renderShard()
                    blockState.isWallHead() -> shards[blockPos] =  blockPos.mutableCopy().renderWallShard()
                }
            }
        }

        on<ChunkUnloadEvent> {
            if (!ShardEsp) return@on

            chunk.forEachBlocks { blockPos, blockState ->
                if (!blockState.isHead() && !blockState.isWallHead()) return@forEachBlocks

                val owner = chunk.getBlockEntity(blockPos, BlockEntityType.SKULL)?.get()?.owner ?: return@forEachBlocks
                val property = owner.properties.asMap()["textures"]?.firstOrNull() ?: return@forEachBlocks
                val value = property.value

                if (value != goldShardHead && value != diamondShardHead) return@forEachBlocks

                printMessage(shards.toString())
                printMessage(blockPos.toString())
                World.removeChildren(shards.remove(blockPos) ?: return@forEachBlocks)
            }
        }
    }
}