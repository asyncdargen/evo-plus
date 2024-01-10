package ru.dargen.evoplus.feature.widget

import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
interface WidgetBase : (Node) -> Unit {

    val node: Node

    fun Node.prepare() {}

    override fun invoke(base: Node) { base.prepare(); base + node }

}