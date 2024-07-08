package ru.dargen.evoplus.util.evo

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.NoteBlock
import net.minecraft.block.enums.Instrument
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.world.cube
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

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

fun BlockPos.renderShard() =
    World+ cube {
        position = v3(x.toDouble() + .75, y.toDouble() + .5, z.toDouble() + 0.25)
        color = Color(0, 255, 255)
        isSeeThrough = true
        size = v3(20.0, 20.0, 20.0)
    }


fun BlockPos.renderWallShard() =
    World+ cube {
        position = v3(x.toDouble() + .3, y.toDouble() + .65, z.toDouble() + 0.2)
        color = Color(0, 255, 255)
        isSeeThrough = true
        size = v3(20.0, 20.0, 20.0)
    }

