package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class BoxNode : RectangleNode() {

    val nonComposingChildren = mutableListOf<Node>()

    init {
        preTransform { _, _ -> recompose() }
    }

    fun recompose() {
        val children = enabledChildren
            .filter { it !in nonComposingChildren }
            .map { it.translation + it.position + it.size * it.scale }
        val sizeX = children.maxOfOrNull { it.x } ?: .0
        val sizeY = children.maxOfOrNull { it.y } ?: .0

        size = v3(sizeX, sizeY, .0)
    }

    override fun removeChildren(children: Collection<Node>) {
        super.removeChildren(children)
        nonComposingChildren.removeAll(children)
    }

    operator fun <N : Node> N.not() = apply { ignore(this) }

    fun ignore(node: Node) {
        if (this != node) {
            nonComposingChildren.add(node)
        }
    }

}

fun box(block: BoxNode.() -> Unit = {}) = BoxNode().apply(block)