package ru.dargen.evoplus.api.render.animation

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import ru.dargen.evoplus.api.render.node.DummyNode
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.WindowInitialized
import ru.dargen.evoplus.util.log
import ru.dargen.evoplus.util.math.fix
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object AnimationRunner {

    val Animations: Table<Node, String, Animation<*>> = HashBasedTable.create()

    init {
        thread(isDaemon = true, name = "Animation-Thread") {
            while (true) runCatching {
                run()

                val delay = if (WindowInitialized) Client.currentFps.fix(10, 200).rateDelay else 50
                Thread.sleep(delay)
            }.exceptionOrNull()?.log("Error while running animations")
        }
    }

    @Synchronized
    fun <N : Node?> run(context: AnimationContext<N>, builder: AnimationBlock<N>): Animation<N> {
        val node = context.safeNode

        Animations.get(node, context.id)?.run {
            cancel()
            finalizeAnimation()
        }

        context.builder()

        val animation = Animation(context)
        Animations.put(node, context.id, animation)

        return animation
    }

    @Synchronized
    private fun run() {
        Animations.values().removeIf { animation ->
            animation.runCatching(Animation<*>::run)
                .exceptionOrNull()
                ?.log("Error while animation run for ${animation.node}:${animation.id}")

            val running = animation.isRunning
            if (!running && !animation.isCancelled) {
                animation.finalizeAnimation()
            }

            return@removeIf !running
        }
    }

    private inline val AnimationContext<*>.safeNode get() = node ?: DummyNode

    private inline val Int.rateDelay get() = (1000.0 / toDouble()).toLong()

}

fun Node.hasAnimation(id: String) = AnimationRunner.Animations.contains(this, id)

fun Node.cancelAnimation(id: String) = AnimationRunner.Animations.get(this, id)?.cancel()

val Node.animations get() = AnimationRunner.Animations.row(this)?.values ?: emptyList<Animation<*>>()

fun <N : Node> animate(
    id: String,
    duration: Duration,
    easing: Easing = Easings.Linear,
    node: N? = null,
    builder: AnimationContext<N>.() -> Unit
) = AnimationContext(node, id, duration, easing).build(builder)

fun <N : Node> animate(
    id: String,
    duration: Double,
    easing: Easing = Easings.Linear,
    node: N? = null,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration.seconds, easing, node, builder)

fun <N : Node> N.animate(
    id: String,
    duration: Duration,
    easing: Easing = Easings.Linear,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration, easing, this, builder)

fun <N : Node> N.animate(
    id: String,
    duration: Double,
    easing: Easing = Easings.Linear,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration.seconds, easing, this, builder)