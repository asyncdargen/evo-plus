package ru.dargen.evoplus.features.esp

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.api.event.world.block.BlockChangeEvent
import ru.dargen.evoplus.api.event.world.block.BlockEntityLoadEvent
import ru.dargen.evoplus.api.event.world.block.BlockEntityUpdateEvent
import ru.dargen.evoplus.api.render.context.WorldContext
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.world.cubeOutline
import ru.dargen.evoplus.api.scheduler.async
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.evo.getBarrel
import ru.dargen.evoplus.util.evo.getLuckyBlock
import ru.dargen.evoplus.util.evo.getShard
import ru.dargen.evoplus.util.evo.isHead
import ru.dargen.evoplus.util.evo.isWallHead
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

object ESPFeature : Feature("esp", "Подсветка", Items.SEA_LANTERN) {

    private val LuckyBlocks = mutableMapOf<BlockPos, Node>()
    private val Shards = mutableMapOf<BlockPos, Node>()
    private val Barrels = mutableMapOf<BlockPos, Node>()

    val LuckyBlocksEsp by settings.boolean("Подсвечивание лаки-блоков") on { state ->
        LuckyBlocks.values.forEach { it.enabled = state }
    }
    val ShardsEsp by settings.boolean("Подсвечивание осколков") on { state ->
        Shards.values.forEach { it.enabled = state }
    }
    val BarrelsEsp by settings.boolean("Подсвечивание бочек") on { state ->
        Barrels.values.forEach { it.enabled = state }
    }

    init {
        on<ChunkLoadEvent> {
            //TODO: make more better than async
            async {
                chunk.blockEntities.forEach { (blockPos, blockEntity) ->
                    recognizeBlock(chunk, blockPos, blockEntity.cachedState)
                }
            }
        }
        on<ChunkUnloadEvent> {
            //TODO: make more better than async
            async {
                chunk.blockEntityPositions.forEach(this@ESPFeature::tryToRemoveBlock)
            }
        }

        on<BlockChangeEvent> {
            tryToRemoveBlock(blockPos)
            recognizeBlock(chunk, blockPos, newState)
        }
        on<BlockEntityLoadEvent> { tryToRecognizeBlock(chunk, blockEntity) }
        on<BlockEntityUpdateEvent> { tryToRecognizeBlock(chunk, blockEntity) }

//        on<WorldRenderEvent> {
//            BlockEntities.forEach {
//                val pos = it.pos
//                if (pos in Shards || pos in LuckyBlocks || pos in Barrels) return@forEach
//
//                recognizeBlock(it.world!!.getWorldChunk(pos), pos, it.cachedState)
//            }
//        }
    }

    private fun tryToRecognizeBlock(chunk: WorldChunk, blockEntity: BlockEntity) {
        if (!ShardsEsp && !LuckyBlocksEsp && !BarrelsEsp) return
        
        val pos = blockEntity.pos
        if (pos in Shards || pos in LuckyBlocks || pos in Barrels) return

        recognizeBlock(chunk, pos, blockEntity.cachedState)
    }

    private fun recognizeBlock(chunk: Chunk, blockPos: BlockPos, blockState: BlockState) {
        if (!ShardsEsp && !LuckyBlocksEsp && !BarrelsEsp) return
        
        val shard = blockState.getShard(blockPos, chunk)
        val luckyBlock = blockState.getLuckyBlock(blockPos, chunk)
        val barrel = blockState.getBarrel()

        when {
            shard != null -> when {
                blockState.isHead() -> Shards[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderLittleCube(shard.color).apply { enabled = ShardsEsp }

                blockState.isWallHead() -> Shards[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderWallLittleCube(shard.color).apply { enabled = ShardsEsp }
            }

            luckyBlock != null -> when {
                blockState.isHead() -> LuckyBlocks[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderLittleCube(luckyBlock.color).apply { enabled = LuckyBlocksEsp }

                blockState.isWallHead() -> LuckyBlocks[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderWallLittleCube(luckyBlock.color).apply { enabled = LuckyBlocksEsp }
            }

            barrel != null -> Barrels[blockPos.mutableCopy()] =
                blockPos.mutableCopy().renderCube(barrel.color).apply { enabled = BarrelsEsp }
        }
    }

    fun tryToRemoveBlock(
        blockPos: BlockPos,
        isShard: Boolean = true,
        isLuckyBlock: Boolean = true,
        isBarrel: Boolean = true
    ) {
        if (!ShardsEsp && !LuckyBlocksEsp && !BarrelsEsp) return
        
        if (isShard) Shards.remove(blockPos)?.let { WorldContext.removeChildren(it) }
        if (isLuckyBlock) LuckyBlocks.remove(blockPos)?.let { WorldContext.removeChildren(it) }
        if (isBarrel) Barrels.remove(blockPos)?.let { WorldContext.removeChildren(it) }
    }

    private fun BlockPos.renderCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + 1, y.toDouble() + 1, z.toDouble())
            this.color = color
            isSeeThrough = true

            size = v3(40.0, 40.0, 40.0)
        }

    private fun BlockPos.renderLittleCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + .75, y.toDouble() + .5, z.toDouble() + 0.25)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }

    private fun BlockPos.renderWallLittleCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + .3, y.toDouble() + .65, z.toDouble() + 0.2)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }
}