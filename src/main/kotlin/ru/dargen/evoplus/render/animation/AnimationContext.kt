package ru.dargen.evoplus.render.animation

import ru.dargen.evoplus.render.animation.property.AnimationProperty
import ru.dargen.evoplus.render.animation.target.AnimationTarget
import ru.dargen.evoplus.render.node.Node
import java.util.*
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

    var nextAnimation: DecomposedAnimationContext<N>? = null,
) {

    companion object {

        val Contexts: Deque<AnimationContext<*>> = LinkedList() /*better then Stack*/

        fun current(): AnimationContext<*> = Contexts.peekLast()

        fun hasContext() = Contexts.isNotEmpty()

    }

    val targetMap = mutableMapOf<AnimationProperty<*>, AnimationTarget<*>>()

    fun target(target: AnimationTarget<*>) {
        targetMap[target.property] = target
    }

    infix fun on(handler: AnimationHandler<N>) = apply { this.handler = handler }

    infix fun after(handler: AnimationHandler<N>) = apply { this.completionHandler = handler }

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

    inline fun build(builder: AnimationBlock<N>): Animation<N> {
        Contexts.addLast(this)
        builder.invoke(this)
        Contexts.pollLast()

        return Animation(this).apply(AnimationHolder::hold)
    }

}