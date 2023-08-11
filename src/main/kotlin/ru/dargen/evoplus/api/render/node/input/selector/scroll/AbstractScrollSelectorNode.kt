package ru.dargen.evoplus.api.render.node.input.selector.scroll

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.TextNode
import ru.dargen.evoplus.api.render.node.input.selector.AbstractSelectorNode
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector
import ru.dargen.evoplus.util.selector.observable

@KotlinOpens
abstract class AbstractScrollSelectorNode<T> : AbstractSelectorNode<T>() {

    override var selector: Selector<T>
        get() = super.selector
        set(value) {
            super.selector = value.observable {
                updateHook()
                updateLabel()
            }
            updateHook()
            updateLabel()
        }

    abstract val hook: Node
    abstract val label: TextNode

    override var nameMapper: Selector<T>.(element: T?) -> String
        get() = super.nameMapper
        set(value) {
            super.nameMapper = value
            updateLabel()
        }

    init {
        color = Colors.Second
    }

    abstract fun updateHook()

    fun updateLabel() {
        label.text = selector.nameMapper(selector.safeSelected)
    }

}