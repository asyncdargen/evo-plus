package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class VBoxNode : AbstractGridBoxNode() {
    override fun recompose() {
        var translateY = indent.y
        var maxX = .0

        children.forEachIndexed { index, node ->
            if (index > 0) {
                translateY += space
            }

            node.align = Relative.LeftTop
            node.origin = Relative.LeftTop
            node.position = v3(indent.x, translateY, .0)

            if (node.size.x * node.scale.x > maxX) {
                maxX = node.size.x * node.scale.x
            }

            translateY += node.size.y * node.scale.y
        }

        if (dependSize) {
            size = if (children.isEmpty()) Vector3() else Vector3(maxX + indent.x * 2, translateY + indent.y)
        }

        if (fixChildSize) {
            enabledChildren.forEach { it.size.x = (if (dependSize) maxX else size.x - indent.x * 2) / it.scale.x }
        }
    }

}

fun vbox(block: VBoxNode.() -> Unit = {}) = VBoxNode().apply(block)