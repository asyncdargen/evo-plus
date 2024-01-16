package ru.dargen.evoplus.util.render

import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import kotlin.math.atan2
import kotlin.math.sqrt

fun Vector3.toDirectionDegrees(): Vector3 {
    val yaw = Math.toDegrees(atan2(-x, z))
    val pitch = Math.toDegrees(atan2(-y, sqrt(x * x + z * z)))
    return v3(pitch, yaw)
}