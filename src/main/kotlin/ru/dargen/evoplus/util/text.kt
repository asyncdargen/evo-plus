package ru.dargen.evoplus.util

private val colorPattern = "(?i)§[\\dA-FK-ORX]".toRegex()

val String.uncolored get() = colorPattern.replace(this, "")