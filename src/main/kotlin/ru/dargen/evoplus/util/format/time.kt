package ru.dargen.evoplus.util.format

import kotlin.time.Duration.Companion.milliseconds

val TextTimePattern = "(\\d*) (ч|мин|сек)".toRegex()
val TextTimeModifiers = mapOf(
    "ч" to 3_600_000L,
    "мин" to 60_000L,
    "сек" to 1000L
)

val Long.asShortTextTime
    get() = if (this < 1000L) "00:00"
    else milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${
            if (hours > 0) "$hours:" else ""
        }${
            if (minutes > 0) "$minutes:".let { if (it.length < 3) "0$it" else it } else "00:"
        }${
            if (seconds > 0) "$seconds" else ""
        }"
    }

val Long.asTextTime
    get() = if (this < 1000L) "сейчас"
    else milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${if (hours > 0) "$hours ч. " else ""}${if (minutes > 0) "$minutes мин. " else ""}${if (seconds > 0) "$seconds сек." else ""}"
    }

val Long.asStrictTextTime
    get() = if (this < 1000L) "сейчас"
    else milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${if (hours > 0) "$hours ч. " else ""}${if (minutes > 0) "$minutes мин. " else ""}${if (hours == 0L && seconds > 0) "$seconds сек." else ""}"
    }


val String.fromTextTime
    get() = TextTimePattern.findAll(this).sumOf { parseTimeTextPart(it.groupValues[1], it.groupValues[2]) }

fun parseTimeTextPart(time: String, modifier: String) =
    TextTimeModifiers.getOrDefault(modifier, 0) * (time.toIntOrNull() ?: 0)