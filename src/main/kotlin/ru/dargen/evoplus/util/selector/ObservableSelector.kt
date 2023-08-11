package ru.dargen.evoplus.util.selector

import ru.dargen.evoplus.util.kotlin.KotlinOpens

typealias SelectorObserver<T> = Selector<T>.() -> Unit

@KotlinOpens
class ObservableSelector<T>(val selector: Selector<T>) : Selector<T> by selector {

    val observers = mutableSetOf<SelectorObserver<T>>()

    override var selected: T
        get() = selector.selected
        set(value) {
            selector.selected = value
            notice()
        }
    override var index: Int
        get() = selector.index
        set(value) {
            selector.index = value
            notice()
        }
    override var elements: List<T>
        get() = selector.elements
        set(value) {
            selector.elements = value
            notice()
        }

    override fun select(element: T) {
        selector.select(element)
        notice()
    }

    override fun selectOn(index: Int) {
        selector.selectOn(index)
        notice()
    }

    override fun shift(shift: Int) {
        selector.selectOn(shift)
        notice()
    }

    fun notice() {
        observers.forEach { it() }
    }

    fun observe(observer: SelectorObserver<T>) = apply { observers.add(observer) }

}

fun <T> Selector<T>.observable() = (if (this is ObservableSelector<T>) this else ObservableSelector(this))

fun <T> Selector<T>.observable(observer: SelectorObserver<T>) =
    observable().observe(observer)