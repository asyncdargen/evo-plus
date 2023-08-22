package ru.dargen.evoplus.util.minecraft

import net.minecraft.text.Text

private val ColorPattern = "(?i)§[\\dA-FK-ORX]".toRegex()
private val HexColorPattern = "&#([a-fA-F0-9]{6})".toRegex()

fun String.asText(): Text = Text.of(this)

fun String.uncolored() = ColorPattern.replace(this, "")

fun String.composeHex() =
    HexColorPattern.replace(this) { it.groupValues[1].fold("&x") { result, char -> "$result&$char" } }