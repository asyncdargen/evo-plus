package ru.dargen.evoplus.api.render.node.scroll

import ru.dargen.evoplus.api.render.animation.property.proxied
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.api.render.node.box.AbstractGridBoxNode
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
abstract class AbstractScrollViewNode : RectangleNode() {

    abstract var box: AbstractGridBoxNode
    abstract var scrollbar: RectangleNode

    var hideSelectorIfUnhovered = false
    var selector by proxied(.0)
    val elements get() = box.children

    protected var lastElementsCount = 0
    val willRecompose get() = (lastElementsCount == elements.size).apply { lastElementsCount = elements.size }

    init {
        preTransform { _, _ -> if (willRecompose) recompose() }
    }

    abstract fun recompose()

    fun addElements(elements: Collection<Node>) = apply { box.addChildren(elements) }

    fun addElements(vararg elements: Node) = apply { box.addChildren(*elements) }

    fun removeElements(elements: Collection<Node>) = apply { box.removeChildren(elements) }

    fun removeElements(vararg elements: Node) = apply { box.removeChildren(*elements) }

}