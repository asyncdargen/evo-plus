package ru.dargen.evoplus.api.render.node.input.selector.scroll

import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.drag
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

class HScrollSelectorNode<T> : AbstractScrollSelectorNode<T>() {

    override val hook = +button {
        isSilent = true
        size = v3(x = 5.0)
        align = ru.dargen.evoplus.api.render.Relative.LeftCenter
        origin = ru.dargen.evoplus.api.render.Relative.Center
    }
    override val label = +text {
        align = ru.dargen.evoplus.api.render.Relative.Center
        origin = ru.dargen.evoplus.api.render.Relative.Center
    }

    override var size: Vector3
        get() = super.size
        set(value) {
            super.size = value
            hook.size.y = value.y + 1.0
        }

    init {
        size = Vector3(100.0, 20.0)

        drag { _, _ ->
            val index = (((MousePosition.x - wholePosition.x) / wholeSize.x) * (selector.size - 1)).toInt()
            if (selector.index != index) {
                selector.selectOn(index)
            }
        }
    }

    override fun updateHook() {
        hook.animate("move", .16, Easings.BackOut) {
            hook.position = v3(
                hook.size.x / 2
                        + (size.x - hook.size.x)
                        * (selector.index / ((selector.size - 1).coerceAtLeast(1)).toDouble())
            )
        }
    }

}

fun <T> hScrollSelector(block: HScrollSelectorNode<T>.() -> Unit = {}) = HScrollSelectorNode<T>().apply(block)