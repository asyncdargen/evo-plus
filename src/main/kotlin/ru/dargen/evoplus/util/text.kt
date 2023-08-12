package ru.dargen.evoplus.util

import net.minecraft.text.Text

private val ColorPattern = "(?i)§[\\dA-FK-ORX]".toRegex()

val String.toText: Text get() = Text.of(this)

fun String.uncolored() = ColorPattern.replace(this, "")