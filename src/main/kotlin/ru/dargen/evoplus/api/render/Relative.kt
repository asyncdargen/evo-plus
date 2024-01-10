package ru.dargen.evoplus.api.render

import ru.dargen.evoplus.util.math.v3

object Relative {

    val LeftTop get() = v3(.0, .0)
    val CenterTop get() = v3(.5, .0)
    val RightTop get() = v3(1.0, .0)

    val LeftCenter get() = v3(.0, .5)
    val Center get() = v3(.5, .5)
    val RightCenter get() = v3(1.0, .5)

    val LeftBottom get() = v3(.0, 1.0)
    val CenterBottom get() = v3(.5, 1.0)
    val RightBottom get() = v3(1.0, 1.0)

    val entries get() = sequence {
        yield(LeftTop)
        yield(CenterTop)
        yield(RightTop)
        yield(LeftCenter)
        yield(Center)
        yield(RightCenter)
        yield(LeftBottom)
        yield(CenterBottom)
        yield(RightBottom)
    }

}