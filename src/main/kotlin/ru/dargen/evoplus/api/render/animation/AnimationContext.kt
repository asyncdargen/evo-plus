package ru.dargen.evoplus.api.render.animation

import io.netty.util.concurrent.FastThreadLocal
import ru.dargen.evoplus.api.render.animation.property.AnimationProperty
import ru.dargen.evoplus.api.render.animation.target.AnimationTarget
import ru.dargen.evoplus.api.render.node.Node
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

typealias AnimationHandler<N> = Animation<N>.() -> Unit
typealias AnimationBlock<N> = AnimationContext<N>.() -> Unit
typealias DecomposedAnimationContext<N> = Pair<AnimationContext<N>, AnimationBlock<N>>

data class AnimationContext<N : Node?>(
    var node: N?, var id: String,
    var duration: Duration, var easing: Easing,

    var handler: AnimationHandler<N>? = null,
    var completionHandler: AnimationHandler<N>? = null,
    var cancellationHandler: AnimationHandler<N>? = null,

    var nextAnimation: DecomposedAnimationContext<N>? = null,
) {

    companion object {

        val Contexts = FastThreadLocal<AnimationContext<*>>()

        fun current(): AnimationContext<*> = Contexts.ifExists

        fun hasContext() = Contexts.isSet

    }

    val targetMap = mutableMapOf<AnimationProperty<*>, AnimationTarget<*>>()

    fun target(target: AnimationTarget<*>) {
        targetMap[target.property] = target
    }

    infix fun on(handler: AnimationHandler<N>) = apply { this.handler = handler }

    fun after(force: Boolean = false, handler: AnimationHandler<N>) = apply {
        completionHandler = handler
        if (force) {
            cancel(handler)
        }
    }

    infix fun cancel(handler: AnimationHandler<N>) = apply { cancellationHandler = handler }

    fun next(
        id: String = this.id,
        duration: Duration,
        easing: Easing = Easings.Linear,
        builder: AnimationContext<N>.() -> Unit
    ) = AnimationContext(node, id, duration, easing).also { nextAnimation = it to builder }

    fun next(
        id: String = this.id,
        duration: Double,
        easing: Easing = Easings.Linear,
        builder: AnimationContext<N>.() -> Unit
    ) = next(id, duration.seconds, easing, builder)

    fun buildNextAnimation() = nextAnimation?.let { (context, builder) -> context.build(builder) }

    inline fun build(crossinline builder: AnimationBlock<N>) = AnimationRunner.run(this) {
        Contexts.set(this)
        builder.invoke(this)
        Contexts.remove()
    }

}