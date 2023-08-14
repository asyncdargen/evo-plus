package ru.dargen.evoplus.util.concurrent

import ru.dargen.evoplus.util.catching
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val Executor = Executors.newScheduledThreadPool(2)

fun async(block: () -> Unit) = Executor.execute(block)

fun every(time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS, block: () -> Unit) =
    Executor.scheduleAtFixedRate(block.catching("Error while scheduling task"), time, time, unit)

fun after(time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS, block: () -> Unit) =
    Executor.schedule(block.catching("Error while scheduling task"), time, unit)