package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class HBoxNode : AbstractGridBoxNode() {

    override fun recompose() {
        var translateX = indent.x
        var maxY = .0

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

        if (dependSize) {
            size = if (children.isEmpty()) Vector3() else Vector3(translateX + indent.x, maxY + indent.y * 2)
        }

        if (fixChildSize) {
            enabledChildren.forEach { it.size.y = (if (dependSize) maxY else size.y - indent.y * 2) / it.scale.y }
        }
    }

}

fun hbox(block: HBoxNode.() -> Unit = {}) = HBoxNode().apply(block)