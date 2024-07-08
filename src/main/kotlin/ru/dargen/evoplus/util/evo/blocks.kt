package ru.dargen.evoplus.util.evo

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.NoteBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.Instrument
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.world.cube
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

private const val GOLDEN_SHARD_HEAD_VALUE =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ=="
private const val DIAMOND_SHARD_HEAD_VALUE =
    "ewogICJ0aW1lc3RhbXAiIDogMTU5ODUyMjU3OTk4MiwKICAicHJvZmlsZUlkIiA6ICJmMDk3N2NmZWZlZmY0ZGM1OGUyMGIzOTVlMjBiYWJkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkaWFtb25kZHVkZTMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE3NjdmYWEzNjZjODA1Nzc5NTJmNWUwMDc4MTU5ZDU5NzdmMzcyMDJmMzhkNDgxN2Q0YTkyNDVhZDQ4YTkwZCIKICAgIH0KICB9Cn0="


fun BlockState.isBarrel() = isNormalBarrel() || isNetherBarrel() || isEndBarrel()
fun BlockState.isDetonatingBarrel() =
    isNormalDetonatingBarrel() || isNetherDetonatingBarrel() || isEndDetonatingBarrel()

fun BlockState.isNormalBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 21
        && get(NoteBlock.POWERED) == false

fun BlockState.isNetherBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 18
        && get(NoteBlock.POWERED) == false

fun BlockState.isEndBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 16
        && get(NoteBlock.POWERED) == false

fun BlockState.isNormalDetonatingBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 22
        && get(NoteBlock.POWERED) == false

fun BlockState.isNetherDetonatingBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 19
        && get(NoteBlock.POWERED) == false

fun BlockState.isEndDetonatingBarrel() = block == Blocks.NOTE_BLOCK
        && get(NoteBlock.INSTRUMENT) === Instrument.FLUTE
        && get(NoteBlock.NOTE) == 17
        && get(NoteBlock.POWERED) == false

fun BlockState.isHead() = block == Blocks.PLAYER_HEAD
fun BlockState.isWallHead() = block == Blocks.PLAYER_WALL_HEAD

fun BlockState.isGoldenShardHead(pos: BlockPos, chunk: Chunk) = (isHead() || isWallHead())
        && pos.getPlayerSkullTextureValue(chunk) == GOLDEN_SHARD_HEAD_VALUE

fun BlockState.isDiamondShardHead(pos: BlockPos, chunk: Chunk) = (isHead() || isWallHead())
        && pos.getPlayerSkullTextureValue(chunk) == DIAMOND_SHARD_HEAD_VALUE

fun BlockPos.getPlayerSkullTextureValue(chunk: Chunk) =
    chunk.getBlockEntity(this, BlockEntityType.SKULL)
        ?.get()?.owner?.properties?.asMap()
        ?.get("textures")?.firstOrNull()?.value

