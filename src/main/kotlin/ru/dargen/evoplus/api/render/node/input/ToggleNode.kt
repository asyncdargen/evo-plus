package ru.dargen.evoplus.api.render.node.input

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

class ToggleNode : RectangleNode() {

    val toggle = +rectangle {
        color = Colors.Primary
        align = Relative.Center
        origin = Relative.Center
    }

    override var size: Vector3
        get() = super.size
        set(value) {
            super.size = value
            toggle.size = size * .6
        }

    var toggled = true
        set(value) {
            field = value
            toggleHandler(value)
            animate("toggle", .15, if (toggled) Easings.BackOut else Easings.BackIn) {
                toggle.scale = v3(1.0, 1.0, 1.0) * (if (toggled) 1.0 else .0)
            }
        }
    var toggleHandler: ToggleNode.(toggled: Boolean) -> Unit = { }

    init {
        size = v3(10.0, 10.0)
        color = Colors.Second

        leftClick { mouse, state ->
            if (state && isHovered) {
                toggled = !toggled
            }
        }
    }

    fun on(handler: ToggleNode.(toggled: Boolean) -> Unit = {}) = apply { toggleHandler = handler }

}

fun toggle(block: ToggleNode.() -> Unit = {}) = ToggleNode().apply(block)