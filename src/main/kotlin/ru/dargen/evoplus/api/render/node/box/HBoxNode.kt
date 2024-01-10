package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class HBoxNode : AbstractGridBoxNode() {

    override fun recompose() {
        var translateX = indent.x
        var maxY = .0

        val children = children.filter { it !in nonComposingChildren }

        children.forEachIndexed { index, node ->
            if (index > 0) {
                translateX += space
            }

            node.align = v3(childrenRelative, .5)
            node.origin = v3(childrenRelative, .5)
            node.position = v3(translateX, .0, .0)

            if (node.size.y * node.scale.y > maxY) {
                maxY = node.size.y * node.scale.y
            }

            translateX += node.size.x * node.scale.x
        }

        if (dependSizeX) {
            size.x = if (children.isEmpty()) .0 else translateX + indent.x
        }
        if (dependSizeY) {
            size.y = if (children.isEmpty()) .0 else maxY + indent.y * 2
        }

        if (fixChildSize) {
            enabledChildren.forEach {
                if (it !in nonComposingChildren) {
                    it.size.y = (if (dependSizeY) maxY else size.y - indent.y * 2) / it.scale.y
                }
            }
        }
    }

}

fun hbox(block: HBoxNode.() -> Unit = {}) = HBoxNode().apply(block)