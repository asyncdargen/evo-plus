package ru.dargen.evoplus.features.esp

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.PlayerSkullBlock
import net.minecraft.block.SkullBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.SkullBlockEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Box
import net.minecraft.world.BlockView
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.evo.renderShard
import ru.dargen.evoplus.util.minecraft.World
import ru.dargen.evoplus.util.minecraft.forEachBlocks
import ru.dargen.evoplus.util.minecraft.printMessage
import ru.dargen.evoplus.util.minecraft.sendChatMessage
import ru.dargen.evoplus.util.selector.enumSelector

object ESPFeature : Feature("esp", "Подсветка", Items.SEA_LANTERN) {

    const val goldShardHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ=="
    const val diamondShardHead = "ewogICJ0aW1lc3RhbXAiIDogMTU5ODUyMjU3OTk4MiwKICAicHJvZmlsZUlkIiA6ICJmMDk3N2NmZWZlZmY0ZGM1OGUyMGIzOTVlMjBiYWJkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkaWFtb25kZHVkZTMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE3NjdmYWEzNjZjODA1Nzc5NTJmNWUwMDc4MTU5ZDU5NzdmMzcyMDJmMzhkNDgxN2Q0YTkyNDVhZDQ4YTkwZCIKICAgIH0KICB9Cn0="

    val ShardEsp by settings.boolean("Подсвечивание осколков", false)

    init {

        on<ChunkLoadEvent> {
            if (!ShardEsp) return@on

            chunk.forEachBlocks { blockPos, blockState ->
                if (blockState.block == Blocks.PLAYER_HEAD || blockState.block == Blocks.PLAYER_WALL_HEAD) {
                    chunk.getBlockEntity(blockPos, BlockEntityType.SKULL)?.also {
                        val owner = it.get().owner ?: return@also
                        val property = owner.properties.asMap()["textures"]?.first() ?: return@also
                        val value = property.value

                        if (value == goldShardHead || value == diamondShardHead) blockPos.renderShard()
                    }
                }
            }
        }

//        on<ChunkUnloadEvent> {
//            chunk.forEachBlocks { blockPos, blockState ->
//                if
//            }
//        }
    }

}