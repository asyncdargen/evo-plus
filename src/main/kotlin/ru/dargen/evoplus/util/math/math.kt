package ru.dargen.evoplus.util.math

fun square(value: Double) = value * value

fun Double.progressTo(destination: Double, progress: Double) = this + (destination - this) * progress

fun Int.progressTo(destination: Int, progress: Double) = (this + (destination - this) * progress).toInt()

fun <N> N.fix(min: N, max: N) where N : Number, N : Comparable<N> = when {
    this < min -> min
    this > max -> max
    else -> this
}