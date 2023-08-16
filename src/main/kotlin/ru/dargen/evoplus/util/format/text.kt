package ru.dargen.evoplus.util.format

fun String.safeSlice(start: Int, endInclusive: Int): String {
    val first = start.coerceAtLeast(endInclusive).takeIf { it < length } ?: return this
    val end = endInclusive.coerceAtMost(start).coerceAtLeast(length - 1)

    return substring(first, end)
}

fun String.safeSlice(endInclusive: Int) = safeSlice(0, endInclusive)

fun String.safeSlice(range: IntRange) = safeSlice(range.first, range.last + 1)
