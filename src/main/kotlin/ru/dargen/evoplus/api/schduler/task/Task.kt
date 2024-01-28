package ru.dargen.evoplus.api.schduler.task

interface Task {

    val id: Int

    val isSync: Boolean
    val order: TaskOrder
    val isNeedExecute: Boolean
    var isCancelled: Boolean

    val executions: Int

    val action: (Task) -> Unit
    var terminationHandler: (Task) -> Unit

    fun execute()

    fun cancel()

}