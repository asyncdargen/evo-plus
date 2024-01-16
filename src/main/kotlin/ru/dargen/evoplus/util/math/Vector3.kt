package ru.dargen.evoplus.util.math

import net.minecraft.util.math.Vec3d
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun scale(x: Double = 1.0, y: Double = 1.0, z: Double = 1.0) = v3(x, y, z)

fun v3(x: Double = .0, y: Double = .0, z: Double = .0) = Vector3(x, y, z)

val Number.v3 get() = Vector3(toDouble())

val Vec3d.asV3 get() = v3(x, y, z)


@KotlinOpens

class Vector3(x: Double = .0, y: Double = .0, z: Double = .0) {
    constructor(value: Double) : this(value, value, value)

    var x: Double = x
    var y: Double = y
    var z: Double = z

    val length: Double
        get() = sqrt(square(x) + square(y) + square(z))

    fun progressTo(x: Double, y: Double, z: Double, progress: Double) =
        clone().set(this.x.progressTo(x, progress), this.y.progressTo(y, progress), this.z.progressTo(z, progress))

    fun progressTo(value: Double, progress: Double) = progressTo(value, value, value, progress)

    fun progressTo(v: Vector3, progress: Double) = v.run { this@Vector3.progressTo(x, y, z, progress) }

    fun fixNaN() = set(if (x.isNaN()) .0 else x, if (y.isNaN()) .0 else y, if (z.isNaN()) .0 else z)

    fun radians() = clone().set(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z))

    fun degrees() = clone().set(Math.toDegrees(x), Math.toDegrees(y), Math.toDegrees(z))

    fun abs() = clone().set(x.absoluteValue, y.absoluteValue, z.absoluteValue)

    fun normalize() = div(length)

    fun dot(x: Double, y: Double, z: Double) = this.x * x + this.y * y + this.z * z

    fun dot(value: Double) = dot(value, value, value)

    fun dot(v: Vector3) = v.run { this@Vector3.dot(x, y, z) }

    fun cross(x: Double, y: Double, z: Double) = clone().apply {
        val crossX = this.y * z - this.z * y
        val crossY = this.z * x - this.x * z
        val crossZ = this.x * y - this.y * x

        this.x = crossX
        this.y = crossY
        this.z = crossZ
    }

    fun cross(value: Double) = cross(value, value, value)

    fun cross(v: Vector3) = v.run { this@Vector3.cross(x, y, z) }

    fun set(x: Double, y: Double, z: Double) = apply {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(value: Double) = set(value, value, value)

    fun set(v: Vector3) = v.run { this@Vector3.set(x, y, z) }

    fun minus(x: Double, y: Double, z: Double) = clone().apply {
        this.x -= x
        this.y -= y
        this.z -= z
    }

    operator fun minus(value: Double) = clone().minus(value, value, value)

    operator fun minus(v: Vector3) = v.run { this@Vector3.minus(x, y, z) }

    fun plus(x: Double, y: Double, z: Double) = clone().apply {
        this.x += x
        this.y += y
        this.z += z
    }

    operator fun plus(value: Double) = plus(value, value, value)

    operator fun plus(v: Vector3) = v.run { this@Vector3.plus(x, y, z) }

    fun times(x: Double, y: Double, z: Double) = clone().apply {
        this.x *= x
        this.y *= y
        this.z *= z
    }

    operator fun times(value: Double) = times(value, value, value)

    operator fun times(v: Vector3) = v.run { this@Vector3.times(x, y, z) }

    fun div(x: Double, y: Double, z: Double) = clone().apply {
        this.x /= x
        this.y /= y
        this.z /= z
    }

    operator fun div(value: Double) = div(value, value, value)

    operator fun div(v: Vector3) = v.run { this@Vector3.div(x, y, z) }

    fun rem(x: Double, y: Double, z: Double) = clone().apply {
        this.x %= x
        this.y %= y
        this.z %= z
    }

    operator fun rem(value: Double) = rem(value, value, value)

    operator fun rem(v: Vector3) = v.run { this@Vector3.rem(x, y, z) }

    operator fun not() = clone() * -1.0

    operator fun unaryMinus() = not()

    fun distance(x: Double, y: Double, z: Double) =
        sqrt(square(this.x - x) + square(this.y - y) + square(this.z - z))

    fun distance(v: Vector3) = distance(v.x, v.y, v.z)

    fun clone() = Vector3(x, y, z)

    fun fixIn(minX: Double, maxX: Double, minY: Double, maxY: Double, minZ: Double, maxZ: Double) =
        set(x.fix(minX, maxX), y.fix(minY, maxY), z.fix(minZ, maxZ))

    fun fixIn(min: Double, max: Double) = fixIn(min, max, min, max, min, max)

    fun fixIn(min: Vector3, max: Vector3) = fixIn(
        min(min.x, max.x), max(min.x, max.x),
        min(min.y, max.y), max(min.y, max.y),
        min(min.z, max.z), max(min.z, max.z),
    )

    fun isBetween(start: Vector3, end: Vector3) =
        start.x <= x && x <= end.x && start.y <= y && y <= end.y && start.z <= z && z <= end.z

    override fun hashCode(): Int {
        var result = 17

        result = result * 31 + x.hashCode()
        result = result * 31 + y.hashCode()
        result = result * 31 + z.hashCode()

        return result
    }

    override fun equals(other: Any?) =
        other is Vector3 && other.x == x && other.y == y && other.z == z

    fun toMap() = mapOf("x" to x, "y" to y, "z" to z)

    override fun toString(): String {
        return "Vector3(x=%.2f, y=%.2f, z=%.2f)".format(x, y, z)
    }

    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z

}