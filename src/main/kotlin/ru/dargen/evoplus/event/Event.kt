package ru.dargen.evoplus.event

import ru.dargen.evoplus.util.kotlin.KotlinOpens

interface Event

@KotlinOpens
abstract class CancellableEvent : Event {

    var isCancelled = false

    fun cancel() {
        isCancelled = true
    }

}