package ru.dargen.evoplus.api.render.node.scroll

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.AbstractGridBoxNode
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.alpha
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.math.v3

class HScrollViewNode : AbstractScrollViewNode() {

    override var box: AbstractGridBoxNode = +hbox {
        align = Relative.LeftTop
        origin = Relative.LeftTop

        dependSize = false
        fixChildSize = true

        isScissor = true
        tick { scissorIndent.set(indent) }
    }
    override var scrollbar = +rectangle {
        size = v3(y = 5.0)
        align = Relative.LeftBottom
        origin = Relative.LeftBottom

        color = Colors.Primary
    }
    override var size: Vector3
        get() = super.size
        set(value) {
            super.size = value
            box.size = value - v3(y = scrollbar.size.y)
        }

    init {
        vWheel { _, wheel ->
            if (isHovered) {
                animate("scroll", .2) {
                    selector = (selector + wheel * -.9 * (minElementSize / boxSize)).fix(.0, 1.0)
                }
            }
        }
        scrollbar.drag { start, delta ->
            animate("scroll", .08) {
                selector = ((MousePosition.y - this@HScrollViewNode.wholePosition.x) / (this@HScrollViewNode.wholeSize.x)).fix(.0, 1.0)
            }
        }
        hover { _, state ->
            scrollbar.animate("hover", .3) {
                scrollbar.color = scrollbar.color
                    .alpha(if (state && scrollbar.size.x != box.size.x || !hideSelectorIfUnhovered) 1.0 else .0)
            }
        }
    }

    val boxSize get() = (box.size.x - box.indent.x * 2)
    val minElementSize get() = enabledChildren.minOfOrNull { it.size.x * it.wholeScale.x + box.space } ?: .0

    override fun recompose() {
        val offset = (box.children.sumOf { it.size.x + box.space } - box.space - boxSize)
            .coerceAtLeast(.0)

        scrollbar.size.x = box.size.x * (boxSize / (offset + boxSize))
        scrollbar.translation.x = (size.x - scrollbar.size.x) * selector

        val minX = box.wholePosition.x + box.indent.x
        val maxX = minX + box.wholeSize.x - box.indent.x * 2
        val elementsOffset = -offset * selector

        box.children.forEach {
            it.translation.x = elementsOffset

            val positionY = it.wholePosition.x
            it.enabled = positionY < maxX && positionY + it.wholeSize.x > minX
        }
    }

}

fun hScrollView(block: HScrollViewNode.() -> Unit = {}) = HScrollViewNode().apply(block)