package ru.dargen.evoplus.api.schduler.task

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.util.concurrent.ScheduledFuture

@KotlinOpens
class AsyncTask(
    id: Int,
    action: (Task) -> Unit,
    terminationHandler: (Task) -> Unit = {},

    repeats: Int,
) : AbstractTask(id, action, repeats) {

    override val isSync = false
    override val order = TaskOrder.ASYNC

    var future: ScheduledFuture<*>? = null
        set(value) {
            field = value
            if (isCanceleld) {
                value?.cancel(true)
            }
        }

    override fun cancel() {
        super.cancel()
        future?.cancel(true)
    }

}