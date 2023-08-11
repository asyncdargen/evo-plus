package ru.dargen.evoplus.api.render.node.input.selector

import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector
import ru.dargen.evoplus.util.selector.emptySelector

@KotlinOpens
abstract class AbstractSelectorNode<T> : RectangleNode() {

    var selector: Selector<T> = emptySelector()
    var nameMapper: Selector<T>.(element: T?) -> String = { it?.toString() ?: "empty" }

    fun mapName(mapper: Selector<T>.(T?) -> String) = apply { nameMapper = mapper }

}