package ru.dargen.evoplus.render.animation

import ru.dargen.evoplus.render.animation.property.AnimationProperty
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.fix
import java.lang.System.currentTimeMillis
import kotlin.time.Duration

data class Animation<N : Node?>(val context: AnimationContext<N>) {

    val id by context::id
    val node by context::node
    val targets by context.targetMap::values

    val startTimestamp: Long = currentTimeMillis()

    var duration = context.duration.inWholeMilliseconds
    val progress
        get() = ((currentTimeMillis() - startTimestamp).toDouble() / duration.toDouble()).fix(.0, 1.0)

    var isCancelled = false
    var isCompleted = false
    val isRunning get() = !isCompleted && !isCancelled

    fun run() {
        val progress = context.easing(progress)

        targets.forEach {
            if (progress >= 1.0) {
                it.property.cast<AnimationProperty<Any>>().value = it.destination
            } else it.progressTo(progress)
        }
        context.handler?.invoke(this)

        if (progress >= 1.0) {
            complete()
        }
    }

    fun cancel() {
        isCancelled = true
    }

    fun complete() {
        isCompleted = true
    }

    fun finalizeAnimation(): Animation<N>? {
        context.completionHandler?.invoke(this)

        return context.buildNextAnimation()
    }

    fun next(
        id: String = this.id,
        duration: Duration,
        easing: Easing = Easings.Linear,
        builder: AnimationContext<N>.() -> Unit
    ) = context.next(id, duration, easing, builder)

    fun next(
        id: String = this.id,
        duration: Double,
        easing: Easing = Easings.Linear,
        builder: AnimationContext<N>.() -> Unit
    ) = context.next(id, duration, easing, builder)

}