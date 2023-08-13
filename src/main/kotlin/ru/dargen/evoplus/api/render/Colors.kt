package ru.dargen.evoplus.api.render

import java.awt.Color

object Colors {

    data object Transparent : Color(0, 0, 0, 0)

    data object White : Color(-1)
    data object Black : Color(0)

    data object Primary : Color(38, 72, 140)
    data object Second : Color(23, 24, 31)

    data object Positive : Color(0, 255, 0)
    data object Negative : Color(255, 0, 0)
    data object Grey : Color(177, 177, 177)

    data object Gold : Color(254, 151, 14)
    data object Green : Color(59, 243, 79)

}