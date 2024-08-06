package ru.dargen.evoplus.api.render.node.scroll

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.AbstractGridBoxNode
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.render.alpha

@KotlinOpens
class VScrollViewNode : AbstractScrollViewNode() {

    override var scrollbar = +rectangle {
        size = v3(x = 5.0)
        align = Relative.RightTop
        origin = Relative.RightTop

        color = Colors.Primary
    }
    override var box: AbstractGridBoxNode = +vbox {
        align = Relative.LeftTop
        origin = Relative.LeftTop

        dependSizeX = false
        dependSizeY = false
        fixChildSize = true

        isScissor = true
        preTransform { _, _ -> scissorIndent.set(indent) }
    }
    override var size: Vector3
        get() = super.size
        set(value) {
            super.size = value
            box.size = value - v3(x = scrollbar.size.x)
        }

    init {
        vWheel { _, wheel ->
            if (isHovered) {
                animate("scroll", .2) {
                    selector = (selector + wheel * -.9 * (minElementSize / boxSize)).fix(.0, 1.0)
                }
                true
            } else false
        }
        scrollbar.drag { start, delta ->
            animate("scroll", .08) {
                selector = ((MousePosition.y - this@VScrollViewNode.wholePosition.y) / (this@VScrollViewNode.wholeSize.y)).fix(.0, 1.0)
            }
        }
        hover { _, state ->
            scrollbar.animate("hover", .3) {
                scrollbar.color = scrollbar.color
                    .alpha(if (state && scrollbar.size.y != box.size.y || !hideSelectorIfUnhovered) 1.0 else .0)
            }
        }
    }

    val boxSize get() = (box.size.y - box.indent.y * 2)
    val minElementSize get() = enabledChildren.minOfOrNull { it.size.y * it.wholeScale.y + box.space } ?: .0

    override fun recompose() {
        val offset = (box.children.sumOf { it.size.y  + box.space } - box.space - boxSize)
            .coerceAtLeast(.0)

        scrollbar.size.y = box.size.y * (boxSize / (offset + boxSize))
        scrollbar.translation.y = (size.y - scrollbar.size.y) * selector

        val minY = box.wholePosition.y + box.indent.y
        val maxY = minY + box.wholeSize.y - box.indent.y * 2
        val elementsOffset = -offset * selector

        box.children.forEach {
            it.translation.y = elementsOffset

            val positionY = it.wholePosition.y
            it.enabled = positionY <= maxY && positionY + it.wholeSize.y >= minY
        }
    }

}

fun vScrollView(block: VScrollViewNode.() -> Unit = {}) = VScrollViewNode().apply(block)