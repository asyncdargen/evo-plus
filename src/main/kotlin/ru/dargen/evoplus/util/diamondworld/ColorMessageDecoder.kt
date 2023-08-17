package ru.dargen.evoplus.util.diamondworld

import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor

object ColorMessageDecoder {

    private val EncodedColorAlternatives: List<Int> = listOf(
        0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 16733695
    )

    private fun color(color: TextColor?) = color?.rgb?.let(EncodedColorAlternatives::indexOf)

    private fun char(color: TextColor?) = color(color)?.let { if (it > 9) " " else it }

    fun decode(text: Text): String {
        //decoding text to char codes sequence
        val buf = (text.style?.color?.let(this::char)?.let(::listOf) ?: emptyList()) + text.siblings
            .mapNotNull(Text::getStyle)
            .mapNotNull(Style::getColor)
            .mapNotNull(this::char)
            .joinToString("")
            .ifEmpty { text.string.replace("[ §]".toRegex(), "").replace('d', ' ') }

            //removing not indexed color
            .replace("-1", "")
            //mapping code -> char -> string
            .split(" ")
            .filter(String::isNotBlank)
            .mapNotNull(String::toIntOrNull)
            .map(Int::toChar)

        return buf.joinToString("")
    }

}