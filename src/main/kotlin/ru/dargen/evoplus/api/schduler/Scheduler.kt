package ru.dargen.evoplus.api.schduler

import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.game.PreTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.schduler.task.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object Scheduler {

    val Executor = Executors.newScheduledThreadPool(2)

    val Id = AtomicInteger()
    val tasks = ConcurrentHashMap<Int, Task>()

    init {
        on<PreTickEvent> {
            tick(TaskOrder.TICK_PRE)
        }
        on<PostTickEvent> {
            tick(TaskOrder.TICK_POST)
        }
    }

    fun registerTask(block: (id: Int) -> Task): Task {
        val id = Id.getAndIncrement()
        val task = block(id)
        tasks[id] = task
        return task;
    }

    fun runAsync(
        delay: Int, period: Int,
        repeats: Int = -1, unit: TimeUnit? = null,
        action: (Task) -> Unit
    ) = registerTask {
        AsyncTask(it, action, repeats = repeats).apply {
            future = Executor.scheduleWithFixedDelay(
                { tryExecute() },
                unit.toNanos(delay.toLong()),
                unit.toNanos(period.toLong()),
                TimeUnit.NANOSECONDS
            )
        }
    }

    fun runTicking(
        delay: Int, period: Int,
        repeats: Int = -1, unit: TimeUnit?,
        isSync: Boolean = true, order: TaskOrder = TaskOrder.TICK_PRE,
        action: (Task) -> Unit
    ) = registerTask { TickingTask(it, action, repeats, delay, period, unit, isSync, order) }

    private fun tick(order: TaskOrder) = tasks.values
        .asSequence()
        .filter { it.order == order }
        .forEach { it.tryExecute() }

    private fun Task.tryExecute() {
        if (isNeedExecute) {
            execute()
        }
        if (isCancelled) {
            tasks.remove(id)
        }
    }

}