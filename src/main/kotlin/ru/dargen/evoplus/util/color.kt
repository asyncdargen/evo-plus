package ru.dargen.evoplus.util

import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.math.progressTo
import java.awt.Color

fun Color.alpha(percent: Double) = alpha((percent * 255).toInt())

fun Color.alpha(alpha: Int) = Color(red, green, blue, alpha.fixCC())

private fun Int.fixCC() = fix(0, 255)

fun Color.progressTo(destination: Color, progress: Double) = Color(
    red.progressTo(destination.red, progress).fixCC(),
    green.progressTo(destination.green, progress).fixCC(),
    blue.progressTo(destination.blue, progress).fixCC(),
    alpha.progressTo(destination.alpha, progress).fixCC()
)