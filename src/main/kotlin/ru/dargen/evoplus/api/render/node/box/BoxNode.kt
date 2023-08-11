package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class BoxNode : RectangleNode() {

    init {
        preTransform { _, _ -> recompose() }
    }

    fun recompose() {
        val children = enabledChildren.map { it.translation + it.position + it.size * it.scale }
        val sizeX = children.maxOfOrNull { it.x } ?: .0
        val sizeY = children.maxOfOrNull { it.y } ?: .0

        size = v3(sizeX, sizeY, .0)

    }

}

fun box(block: BoxNode.() -> Unit = {}) = BoxNode().apply(block)