package ru.dargen.evoplus.render

import ru.dargen.evoplus.util.math.Vector3

object Relative {

    val LeftTop get() = Vector3(.0, .0)
    val CenterTop get() = Vector3(.5, .0)
    val RightTop get() = Vector3(1.0, .0)

    val LeftCenter get() = Vector3(.0, .5)
    val Center get() = Vector3(.5, .5)
    val RightCenter get() = Vector3(1.0, .5)

    val LeftBottom get() = Vector3(.0, 1.0)
    val CenterBottom get() = Vector3(.5, 1.0)
    val RightBottom get() = Vector3(1.0, 1.0)

}