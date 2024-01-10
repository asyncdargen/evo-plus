package ru.dargen.evoplus.api.schduler.task

import ru.dargen.evoplus.api.schduler.Scheduler.Executor
import ru.dargen.evoplus.util.currentNanos
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

@KotlinOpens
class TickingTask(
    id: Int,
    action: (Task) -> Unit,

    repeats: Int,
    delay: Int, val period: Int,
    val timeUnit: TimeUnit? = null,

    override val isSync: Boolean,
    override val order: TaskOrder
) : AbstractTask(id, action, repeats) {

    init {
        require(order != TaskOrder.ASYNC) { "Illegal ticking task execution order" }
    }

    override val isNeedExecute: Boolean
        get() = super.isNeedExecute && currentNanos >= nextTick

    var nextTick = currentNanos + timeUnit.toNanos(delay.toLong())

    override fun execute0() {
        nextTick = currentNanos + timeUnit.toNanos(period.toLong())
        if (isSync) action(this) else Executor.execute { action(this) }
    }

}

fun TimeUnit?.toNanos(time: Long) = this?.toNanos(time) ?: MILLISECONDS.toNanos(time * 50)