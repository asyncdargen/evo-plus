package ru.dargen.evoplus.util

import ru.dargen.evoplus.Logger
import java.util.logging.Level

fun Throwable.log(message: String) = Logger.log(Level.SEVERE, message, this)