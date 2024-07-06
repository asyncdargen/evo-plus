package ru.dargen.evoplus.util.evo

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.MushroomBlock

fun BlockState.isBarrel() = isNormalBarrel() || isNetherBarrel() || isEndBarrel()

fun BlockState.isNormalBarrel() = block == Blocks.MUSHROOM_STEM
        && get(MushroomBlock.NORTH) == false
        && get(MushroomBlock.EAST) == false
        && get(MushroomBlock.SOUTH) == false
        && get(MushroomBlock.WEST) == false
        && get(MushroomBlock.UP) == false
        && get(MushroomBlock.DOWN) == false

fun BlockState.isNetherBarrel() = block == Blocks.MUSHROOM_STEM
        && get(MushroomBlock.NORTH) == true
        && get(MushroomBlock.EAST) == true
        && get(MushroomBlock.SOUTH) == false
        && get(MushroomBlock.WEST) == false
        && get(MushroomBlock.UP) == false
        && get(MushroomBlock.DOWN) == false
 
fun BlockState.isEndBarrel() = block == Blocks.MUSHROOM_STEM
        && get(MushroomBlock.NORTH) == true
        && get(MushroomBlock.EAST) == true
        && get(MushroomBlock.SOUTH) == true
        && get(MushroomBlock.WEST) == true
        && get(MushroomBlock.UP) == false
        && get(MushroomBlock.DOWN) == false