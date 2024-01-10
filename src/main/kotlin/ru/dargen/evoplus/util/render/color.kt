package ru.dargen.evoplus.util.render

import net.minecraft.util.math.ColorHelper.Argb.*
import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.math.progressTo
import java.awt.Color

data class RGBA<T : Number>(val red: T, val green: T, val blue: T, val alpha: T)

fun Color.alpha(percent: Double) = alpha((percent * 255).toInt())

fun Color.alpha(alpha: Int) = Color(red, green, blue, alpha.fixCC())

fun Int.decomposeColor() = RGBA(getRed(this), getGreen(this), getBlue(this), getAlpha(this))

fun Int.decomposeColorFloat() = RGBA(
    getRed(this).toFloat() / 255f, getGreen(this).toFloat() / 255f,
    getBlue(this).toFloat() / 255f, getAlpha(this).toFloat() / 255f
)

fun Color.decompose() = rgb.decomposeColor()

fun Color.decomposeFloat() = rgb.decomposeColorFloat()

private fun Int.fixCC() = fix(0, 255)

fun Color.progressTo(destination: Color, progress: Double) = Color(
    red.progressTo(destination.red, progress).fixCC(),
    green.progressTo(destination.green, progress).fixCC(),
    blue.progressTo(destination.blue, progress).fixCC(),
    alpha.progressTo(destination.alpha, progress).fixCC()
)