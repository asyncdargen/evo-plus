package ru.dargen.evoplus.render.animation

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import ru.dargen.evoplus.render.node.DummyNode
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.WindowInitialized
import ru.dargen.evoplus.util.log
import ru.dargen.evoplus.util.math.fix
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object AnimationHolder {

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

    fun hold(animation: Animation<*>) {
        val node = animation.safeNode

        Animations.get(node, animation.id)?.run {
            cancel()
            finalizeAnimation()
        }

        Animations.put(node, animation.id, animation)
    }

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

    private inline val Animation<*>.safeNode get() = node ?: DummyNode

    private inline val Int.rateDelay get() = (1000.0 / toDouble()).toLong()

}

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