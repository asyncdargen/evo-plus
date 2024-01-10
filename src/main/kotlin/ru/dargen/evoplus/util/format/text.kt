package ru.dargen.evoplus.util.format

val Boolean.color get() = if (this) "§a" else "§c"

val FixDoublePattern = "\\.?0?0?0?0\$".toRegex()
fun String.reduceFloatingZero() = replace(FixDoublePattern, "")
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

