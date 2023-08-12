package ru.dargen.evoplus.util.format

import kotlin.time.Duration.Companion.milliseconds

val TextTimePattern = "(\\d*) (ч|мин|сек)".toRegex()
val TextTimeModifiers = mapOf(
    "ч" to 3_600_000L,
    "мин" to 60_000L,
    "сек" to 1000L
)

val Long.asTextTime
    get() = if (this < 1000L) "сейчас"
    else milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${if (hours > 0) "$hours ч. " else ""}${if (minutes > 0) "$minutes мин. " else ""}${if (seconds > 0) "$seconds сек." else ""}"
    }

val String.fromTextTime
    get() = TextTimePattern.findAll(this)
        .fold(0L) { acc, match -> acc + parseTimeTextPart(match.groupValues[1], match.groupValues[2]) }

fun parseTimeTextPart(time: String, modifier: String) =
    TextTimeModifiers.getOrDefault(modifier, 0) * (time.toIntOrNull() ?: 0)