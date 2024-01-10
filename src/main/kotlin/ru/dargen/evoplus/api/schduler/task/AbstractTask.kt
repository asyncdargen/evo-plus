package ru.dargen.evoplus.api.schduler.task

import ru.dargen.evoplus.api.schduler.task.Task
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
abstract class AbstractTask(
    override val id: Int,

    override val action: (Task) -> Unit,

    val repeats: Int
) : Task {

    var executions = 0

    override val isNeedExecute get() = !isCanceleld
    override var isCanceleld = false

    override var terminationHandler: (Task) -> Unit = {}

    infix fun onTerminate(block: (Task) -> Unit) {
        terminationHandler = block
    }

    protected fun execute0() {
        action(this)
    }

    override fun execute() {
        ++executions
        catch("Error while task executing: $id") { execute0() }
        if (repeats in 0..executions) {
            cancel()
        }
    }

    override fun cancel() {
        if (!isCanceleld) {
            isCanceleld = true
            catch("Error while processing task termination handler: $id") { terminationHandler(this) }
        }
    }

}