package ru.dargen.evoplus.util.evo

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.NoteBlock
import net.minecraft.block.enums.Instrument

fun BlockState.isBarrel() = isNormalBarrel() || isNetherBarrel() || isEndBarrel()

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