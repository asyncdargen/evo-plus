package ru.dargen.evoplus.util

import ru.dargen.evoplus.Logger
import java.util.logging.Level

fun Throwable.log(message: String) = Logger.error(message, this)

typealias Runnable = () -> Unit

fun Runnable.catching(message: String): Runnable = { catch(message, this) }

fun catch(message: String = "", block: Runnable) = runCatching(block).exceptionOrNull()?.log(message)

val currentMillis get() = System.currentTimeMillis()
val currentNanos get() = System.nanoTime()