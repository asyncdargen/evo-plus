package ru.dargen.evoplus.util.selector

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.fix

@KotlinOpens
class ListSelector<T>(elements: List<T> = emptyList(), index: Int = 0) : Selector<T> {

    override var selected: T
        get() = elements[index]
        set(value) {
            index = elements.indexOf(value).coerceAtLeast(0)
        }
    override val safeSelected: T?
        get() = selected.takeIf { hasSelected }
    override var elements = elements
        set(value) {
            field = value
            index = fixIndex()
        }

    override val size: Int
        get() = elements.size
    override var index = fixIndex(index)
        set(value) {
            field = fixIndex(value)
        }
    override val hasSelected: Boolean
        get() = elements.isNotEmpty()

    override fun select(element: T) {
        selected = element
    }

    override fun selectOn(index: Int) {
        this.index = index
    }

    override fun shift(shift: Int) {
        index += shift
    }

    fun fixIndex(index: Int = this.index) = index.fix(0, elements.size - 1)

}

fun <E> Iterable<E>.toSelector(index: Int = 0 ) = toList().toSelector()

fun <E> Collection<E>.toSelector(index: Int = 0) = ListSelector(toList(), if (index == -1) size - 1 else index)

fun <E> selector(elements: Collection<E>, index: Int = 0) = elements.toSelector(index)

fun <E> selector(vararg elements: E, index: Int = 0) = elements.toList().toSelector(index)