package ru.dargen.evoplus.util.format

import java.text.DecimalFormat

val SpacingNumberFormat = DecimalFormat("### ###")
fun Int.spacing() = SpacingNumberFormat.format(this)


val FormatDividers = mutableMapOf(
    1.0 to "",
    1e3 to "K",
    1e6 to "M",
    1e9 to "B",
    1e12 to "T",
    1e15 to "q",
    1e18 to "Qi",
)

private val formats = hashMapOf("###.##" to DecimalFormat("###.##"))

fun Double.format(
    pattern: String = "###.##",
    withSymbols: Boolean = true
) = formats.computeIfAbsent(pattern, ::DecimalFormat).run {
    if (!withSymbols) return@run format(this@format)

    val divider = FormatDividers.keys.run { lastOrNull { this@format / it >= 1 } ?: first() }
    return@run "${format(this@format / divider).reduceFloatingZero()}${FormatDividers[divider]}"
}