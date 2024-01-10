package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class VBoxNode : AbstractGridBoxNode() {

    override fun recompose() {
        var translateY = indent.y
        var maxX = .0

        val children = children.filter { it !in nonComposingChildren }

        children.forEachIndexed { index, node ->
            if (index > 0) {
                translateY += space
            }

            node.align = v3(childrenRelative)
            node.origin = v3(childrenRelative)
            node.position = v3(indent.x, translateY, .0)

            if (node.size.x * node.scale.x > maxX) {
                maxX = node.size.x * node.scale.x
            }

            translateY += node.size.y * node.scale.y
        }

        if (dependSizeX) {
            size.x = if (children.isEmpty()) .0 else maxX + indent.x * 2
        }
        if (dependSizeY) {
            size.y = if (children.isEmpty()) .0 else translateY + indent.y
        }

        if (fixChildSize) {
            enabledChildren.forEach {
                if (it !in nonComposingChildren) {
                    it.size.x = (if (dependSizeX) maxX else size.x - indent.x * 2) / it.scale.x
                }
            }
        }
    }

}

fun vbox(block: VBoxNode.() -> Unit = {}) = VBoxNode().apply(block)