package ru.dargen.evoplus.render.animation.target

import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.progressTo
import ru.dargen.evoplus.util.progressTo
import java.awt.Color

private typealias Vector = Vector3
private typealias JDouble = Double
private typealias JColor = Color

interface AnimationTargetType<C> {

    fun progressTo(initial: C, destination: C, progress: JDouble): C

    companion object {

        val TypeMap = mutableMapOf(
            Vector::class.java to Vector3,
            JDouble::class.java to Double,
            java.lang.Double::class.java to Double,
            JColor::class.java to Color,
        )

        fun <T> forClass(clazz: Class<T>): AnimationTargetType<T> =
            (TypeMap[clazz] ?: throw IllegalArgumentException("Unsupported animation target type $clazz")).cast()

        fun <T : Any> forTarget(target: T) = forClass(target.javaClass)

    }

    sealed class DelegateAnimationTargetType<C>(
        val calculator: (initial: C, destination: C, progress: JDouble) -> C
    ) : AnimationTargetType<C> {

        override fun progressTo(initial: C, destination: C, progress: JDouble) =
            calculator(initial, destination, progress)

    }

    data object Vector3 : DelegateAnimationTargetType<Vector>(Vector::progressTo)
    data object Double : AnimationTargetType<JDouble> {

        override fun progressTo(initial: JDouble, destination: JDouble, progress: JDouble) =
            initial.progressTo(destination, progress)

    }

    data object Color : AnimationTargetType<JColor> {

        override fun progressTo(initial: JColor, destination: JColor, progress: JDouble) =
            initial.progressTo(destination, progress)

    }

}