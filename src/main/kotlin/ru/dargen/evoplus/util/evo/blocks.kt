package ru.dargen.evoplus.util.evo

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.NoteBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.enums.Instrument
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import ru.dargen.evoplus.api.render.Colors
import java.awt.Color

fun BlockState.isBarrel() = Barrel.isBarrel(this)
fun BlockState.isDetonatingBarrel() = Barrel.isDetonatingBarrel(this)

fun BlockState.isHead() = block === Blocks.PLAYER_HEAD
fun BlockState.isWallHead() = block === Blocks.PLAYER_WALL_HEAD

fun BlockPos.getPlayerSkullTextureValue(chunk: Chunk) =
    chunk.getBlockEntity(this, BlockEntityType.SKULL)
        ?.get()?.owner?.properties?.asMap()
        ?.get("textures")?.firstOrNull()?.value

fun BlockState.getShard(pos: BlockPos, chunk: Chunk) = Shard.entries.firstOrNull {
    it.isThis(this, pos, chunk)
}

fun BlockState.getLuckyBlock(pos: BlockPos, chunk: Chunk) = LuckyBlock.entries.firstOrNull {
    it.isThis(this, pos, chunk)
}

fun BlockState.getBarrel() = Barrel.entries.firstOrNull {
    it.isThis(this)
}

enum class Barrel(val color: Color, val instrument: Instrument, val note: Int, val powered: Boolean) {
    NORMAL(Colors.Green, Instrument.FLUTE, 21, false),
    NORMAL_DETONATING(Colors.Green, Instrument.FLUTE, 22, false),
    NETHER(Colors.Green, Instrument.FLUTE, 18, false),
    NETHER_DETONATING(Colors.Green, Instrument.FLUTE, 19, false),
    END(Colors.Green, Instrument.FLUTE, 16, false),
    END_DETONATING(Colors.Green, Instrument.FLUTE, 17, false),
    ;
    
    fun isThis(blockState: BlockState) = blockState.run {
        block === Blocks.NOTE_BLOCK
            && get(NoteBlock.INSTRUMENT) === instrument
            && get(NoteBlock.NOTE) == note
            && get(NoteBlock.POWERED) == powered
    }
    
    companion object {
        fun isBarrel(blockState: BlockState) = NORMAL.isThis(blockState) || NETHER.isThis(blockState) || END.isThis(blockState)
        fun isDetonatingBarrel(blockState: BlockState) = NORMAL_DETONATING.isThis(blockState) || NETHER_DETONATING.isThis(blockState) || END_DETONATING.isThis(blockState)
    }
}

enum class Shard(val color: Color, val value: String) {
    
    GOLDEN(Colors.Gold, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRiZjg5M2ZjNmRlZmFkMjE4Zjc4MzZlZmVmYmU2MzZmMWMyY2MxYmI2NTBjODJmY2NkOTlmMmMxZWU2In19fQ=="),
    DIAMOND(Colors.Diamond, "ewogICJ0aW1lc3RhbXAiIDogMTU5ODUyMjU3OTk4MiwKICAicHJvZmlsZUlkIiA6ICJmMDk3N2NmZWZlZmY0ZGM1OGUyMGIzOTVlMjBiYWJkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkaWFtb25kZHVkZTMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE3NjdmYWEzNjZjODA1Nzc5NTJmNWUwMDc4MTU5ZDU5NzdmMzcyMDJmMzhkNDgxN2Q0YTkyNDVhZDQ4YTkwZCIKICAgIH0KICB9Cn0="),
    ;
    
    fun isThis(blockState: BlockState, pos: BlockPos, chunk: Chunk) =
        (blockState.isHead() || blockState.isWallHead()) && pos.getPlayerSkullTextureValue(chunk) == value
}

enum class LuckyBlock(val color: Color, val value: String) {
    
    BASE(Colors.Yellow, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM4YzBkMmYxZWMyNjc1NGRjYTNjN2NkYWUzMWYxZjE2NDg4M2Q0NTNlNjg4NjQzZGEwNDc1NjhlN2ZhNWNjOSJ9fX0="),
    RARE(Colors.Deepskyblue, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmUwMDJkOTc3MjNiOGNjOTgwMmQzMGZlOGU0Y2VmMzYxZTU2Y2YyZTQ5YWU5MWYyNzRkYTcyZjQ3ODEzNDExOCJ9fX0="),
    LEGENDARY(Colors.Purple, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA2ZWExMDRjYjliZTcwM2NjZWQxYjFmNTY1Mjg2NzUyZTI3MTc1MmM1YWM4NWU4MTEzYjNlMmRjNDM1MmMyMCJ9fX0="),
    ;
    
    fun isThis(blockState: BlockState, pos: BlockPos, chunk: Chunk) =
        (blockState.isHead() || blockState.isWallHead()) && pos.getPlayerSkullTextureValue(chunk) == value
}

