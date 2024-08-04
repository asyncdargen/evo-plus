package ru.dargen.evoplus.features.esp

import net.minecraft.block.BlockState
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.BlockChangeEvent
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.world.cube
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.evo.getBarrel
import ru.dargen.evoplus.util.evo.getLuckyBlock
import ru.dargen.evoplus.util.evo.getShard
import ru.dargen.evoplus.util.evo.isHead
import ru.dargen.evoplus.util.evo.isWallHead
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.forEachBlocks
import java.awt.Color

object ESPFeature : Feature("esp", "Подсветка", Items.SEA_LANTERN) {
    
    private val LuckyBlocks = mutableMapOf<BlockPos, Node>()
    private val Shards = mutableMapOf<BlockPos, Node>()
    private val Barrels = mutableMapOf<BlockPos, Node>()
    
    val LuckyBlocksEsp by settings.boolean("Подсвечивание лаки-блоков", false) on { state ->
        LuckyBlocks.values.forEach { it.enabled = state }
    }
    val ShardsEsp by settings.boolean("Подсвечивание осколков", false) on { state ->
        Shards.values.forEach { it.enabled = state }
    }
    val BarrelsEsp by settings.boolean("Подсвечивание бочек", false) on { state ->
        Barrels.values.forEach { it.enabled = state }
    }
    
    init {
        on<ChunkLoadEvent> {
            chunk.forEachBlocks { blockPos, blockState ->
                recognizeBlock(chunk, blockPos, blockState)
            }
        }
        
        on<ChunkUnloadEvent> {
            chunk.forEachBlocks { blockPos, blockState ->
                tryToRemoveBlock(blockPos,
                    blockState.getShard(blockPos, chunk) != null,
                    blockState.getLuckyBlock(blockPos, chunk) != null,
                    blockState.getShard(blockPos, chunk) != null
                )
            }
        }
        
        on<BlockChangeEvent> {
            tryToRemoveBlock(blockPos,
                oldState?.getShard(blockPos, chunk) != null,
                oldState?.getLuckyBlock(blockPos, chunk) != null,
                oldState?.getBarrel() != null
            )
            recognizeBlock(chunk, blockPos, newState)
        }
    }
    
    fun recognizeBlock(chunk: Chunk, blockPos: BlockPos, blockState: BlockState) {
        val shard = blockState.getShard(blockPos, chunk)
        val luckyBlock = blockState.getLuckyBlock(blockPos, chunk)
        val barrel = blockState.getBarrel()
        
        when {
            shard != null -> when {
                blockState.isHead() -> Shards[blockPos.mutableCopy()] = blockPos.mutableCopy().renderLittleCube(shard.color).apply { enabled = ShardsEsp }
                blockState.isWallHead() -> Shards[blockPos.mutableCopy()] = blockPos.mutableCopy().renderWallLittleCube(shard.color).apply { enabled = ShardsEsp }
            }
            
            luckyBlock != null -> when {
                blockState.isHead() -> LuckyBlocks[blockPos.mutableCopy()] = blockPos.mutableCopy().renderLittleCube(luckyBlock.color).apply { enabled = LuckyBlocksEsp }
                blockState.isWallHead() -> LuckyBlocks[blockPos.mutableCopy()] = blockPos.mutableCopy().renderWallLittleCube(luckyBlock.color).apply { enabled = LuckyBlocksEsp }
            }
            
            barrel != null -> Barrels[blockPos.mutableCopy()] = blockPos.mutableCopy().renderCube(barrel.color).apply { enabled = BarrelsEsp }
        }
    }
    
    fun tryToRemoveBlock(blockPos: BlockPos, isShard: Boolean, isLuckyBlock: Boolean, isBarrel: Boolean) {
        if (isShard) Shards.remove(blockPos)?.let { World.removeChildren(it) }
        if (isLuckyBlock) LuckyBlocks.remove(blockPos)?.let { World.removeChildren(it) }
        if (isBarrel) Barrels.remove(blockPos)?.let { World.removeChildren(it) }
    }
    
    private fun BlockPos.renderCube(color: Color) =
        World + cube {
            position = v3(x.toDouble() + 1, y.toDouble() + 1, z.toDouble())
            this.color = color
            isSeeThrough = true
            
            size = v3(40.0, 40.0, 40.0)
        }
    
    private fun BlockPos.renderLittleCube(color: Color) =
        World + cube {
            position = v3(x.toDouble() + .75, y.toDouble() + .5, z.toDouble() + 0.25)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }
    
    private fun BlockPos.renderWallLittleCube(color: Color) =
        World + cube {
            position = v3(x.toDouble() + .3, y.toDouble() + .65, z.toDouble() + 0.2)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }
}