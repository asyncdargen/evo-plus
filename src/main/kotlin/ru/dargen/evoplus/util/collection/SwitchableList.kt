package ru.dargen.evoplus.util.collection

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.util.*

@KotlinOpens
class SwitchableList<E>(collection: Collection<E>) : LinkedList<E>(collection) {
    constructor(array: Array<out E>) : this(array.toList())
    constructor() : this(emptyList())

    fun switch(): E = synchronized(this) {
        removeFirst().apply { addLast(this) }
    }

}