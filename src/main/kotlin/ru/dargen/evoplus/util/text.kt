package ru.dargen.evoplus.util

private val ColorPattern = "(?i)§[\\dA-FK-ORX]".toRegex()

fun String.uncolored() = ColorPattern.replace(this, "")