package ru.dargen.evoplus.util.minecraft

import net.minecraft.text.Text

private val ColorPattern = "(?i)§[\\dA-FK-ORX]".toRegex()

val String.asText: Text get() = Text.of(this)

fun String.uncolored() = ColorPattern.replace(this, "")