package ru.dargen.evoplus.render.animation

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

typealias Easing = (Double) -> Double

object Easings {

    val Linear: Easing = { it }
    val QuadIn: Easing = powIn(2.0)
    val QuadOut: Easing = powOut(2.0)
    val QuadBoth: Easing = powBoth(2.0)

    val CubicIn: Easing = powIn(3.0)
    val CubicOut: Easing = powOut(3.0)
    val CubicBoth: Easing = powBoth(3.0)

    val QuartIn: Easing = powIn(4.0)
    val QuartOut: Easing = powOut(4.0)
    val QuartBoth: Easing = powBoth(4.0)

    val QuintIn: Easing = powIn(5.0)
    val QuintOut: Easing = powOut(5.0)
    val QuintBoth: Easing = powBoth(5.0)

    val SineIn: Easing = { 1.0 - cos(it * Math.PI / 2.0) }
    val SineOut: Easing = { sin(it * Math.PI / 2.0) }
    val SineBoth: Easing = { -(cos(Math.PI * it) - 1.0) / 2.0 }

    val CircIn: Easing = { 1.0 - sqrt(1.0 - it.pow(2.0)) }
    val CircOut: Easing = { sqrt(1.0 - (it - 1.0).pow(2.0)) }
    val CircBoth: Easing = {
        if (it < 0.5) {
            (1.0 - sqrt(1.0 - (2.0 * it).pow(2.0))) / 2.0
        } else {
            (sqrt(1.0 - (-2.0 * it + 2.0).pow(2.0)) + 1.0) / 2.0
        }
    }

    val ElasticIn: Easing = {
        if (it == 0.0 || it == 1.0) {
            it
        } else {
            (-2.0.pow(10 * it - 10)) * sin((it * 10 - 10.75) * ((2 * Math.PI) / 3))
        }
    }

    val ElasticOut: Easing = {
        if (it == 0.0 || it == 1.0) {
            it
        } else {
            (2.0.pow(-10.0 * it) * sin((it * 10.0 - 0.75) * (2.0 * Math.PI / 3.0)) + 1.0)
        }
    }

    val ElasticBoth: Easing = {
        if (it == 0.0 || it == 1.0) {
            it
        } else if (it < 0.5) {
            (-(2.0.pow(20.0 * it - 10.0) * sin((20.0 * it - 11.125) * (2.0 * Math.PI / 4.5))) / 2.0)
        } else {
            (2.0.pow(-20.0 * it + 10.0) * sin((20.0 * it - 11.125) * (2.0 * Math.PI / 4.5)) / 2.0 + 1.0)
        }
    }

    val ExpoIn: Easing = {
        if (it != 0.0) {
            2.0.pow(10.0 * it - 10.0)
        } else {
            it
        }
    }

    val ExpoOut: Easing = {
        if (it != 1.0) {
            1.0 - 2.0.pow(-10.0 * it)
        } else {
            it
        }
    }

    val ExpoBoth: Easing = {
        if (it == 0.0 || it == 1.0) {
            it
        } else if (it < 0.5) {
            2.0.pow(20.0 * it - 10.0) / 2.0
        } else {
            (2.0 - 2.0.pow(-20.0 * it + 10)) / 2.0
        }
    }

    val BackIn: Easing = { 2.70158 * it.pow(3.0) - 1.70158 * it.pow(2.0) }
    val BackOut: Easing = { 1.0 + 2.70158 * (it - 1.0).pow(3.0) + 1.70158 * (it - 1.0).pow(2.0) }
    val BackBoth: Easing = {
        if (it < 0.5) {
            (2.0 * it).pow(2.0) * ((1.70158 * 1.525 + 1.0) * 2.0 * it - 1.70158 * 1.525) / 2.0
        } else {
            ((2.0 * it - 2.0).pow(2.0) * ((1.70158 * 1.525 + 1.0) * (it * 2.0 - 2.0) + 1.70158 * 1.525) + 2.0) / 2.0
        }
    }

    val BounceOut: Easing = { x ->
        val n1 = 7.5625
        val d1 = 2.75
        when {
            x < 1.0 / d1 -> n1 * x.pow(2.0)
            x < 2.0 / d1 -> n1 * (x - 1.5 / d1).pow(2.0) + 0.75
            x < 2.5 / d1 -> n1 * (x - 2.25 / d1).pow(2.0) + 0.9375
            else -> n1 * (x - 2.625 / d1).pow(2.0) + 0.984375
        }
    }

    val BounceIn: Easing = { 1.0 - BounceOut(1.0 - it) }

    val BounceBoth: Easing = {
        if (it < 0.5) {
            (1 - BounceOut(1.0 - 2.0 * it)) / 2.0
        } else {
            (1 + BounceOut(2.0 * it - 1.0)) / 2.0
        }
    }

    private fun powIn(n: Double): Easing = { it.pow(n) }

    private fun powOut(n: Double): Easing = { 1.0 - (1.0 - it).pow(n) }

    private fun powBoth(n: Double): Easing =
        { if (it < .5) 2.0.pow(n - 1.0) * it.pow(n) else 1.0 - (-2.0 * it + 2.0).pow(n) / 2.0 }
}
