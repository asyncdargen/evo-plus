package ru.dargen.evoplus.util.format

import kotlin.math.abs

private val Indexes = intArrayOf(2, 0, 1, 1, 1, 2)

val Boolean.color get() = if (this) "§a" else "§c"

val FixDoublePattern = "\\.?0?0?0?0\$".toRegex()
fun String.reduceFloatingZero() = if ('.' in this) replace(FixDoublePattern, "") else this
fun Double.fix(floating: Int = 2) = "%.${floating}f".format(this)
    .replace(",", ".")
    .reduceFloatingZero()

fun String.wrap(wrap: String) = "$wrap$this$wrap"

fun String.safeSlice(start: Int, endInclusive: Int): String {
    val first = start.coerceAtLeast(endInclusive).takeIf { it < length } ?: return this
    val end = endInclusive.coerceAtMost(start).coerceAtLeast(length - 1)

    return substring(first, end)
}

fun String.safeSlice(endInclusive: Int) = safeSlice(0, endInclusive)

fun String.safeSlice(range: IntRange) = safeSlice(range.first, range.last + 1)

fun Int.nounEndings(vararg nouns: String) =
    nouns[if (this % 100 in 5..19) 2 else Indexes[if (this % 10 < 5) abs(this) % 10 else 5]]

