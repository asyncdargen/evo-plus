package ru.dargen.evoplus.util.selector

interface Selector<T> {

    var selected: T
    val safeSelected: T?
    var elements: List<T>

    var index: Int
    val hasSelected: Boolean
    val size: Int

    fun select(element: T)

    fun selectOn(index: Int)

    fun shift(shift: Int)

}

fun <T> emptySelector() = ListSelector<T>(emptyList())