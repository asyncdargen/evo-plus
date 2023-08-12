package ru.dargen.evoplus.feature.misc

import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.OverlayContext
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.minusAssign
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.math.v3

object Notifies {

    val Box = OverlayContext + hbox {
        align = Relative.RightTop
        origin = Relative.RightTop

        fixChildSize = true

        indent = v3(8.0, 8.0, .0)
        space = 8.0
    }

    fun show(text: String, delay: Double = 9.0, block: Node.() -> Unit = {}) = hbox {
        translation = v3(x = 200.0)
        indent = v3(8.0, 8.0, .0)
        space = 8.0

        fixChildSize = true

        +text(text)
        +vbox(block)

        animate("show", .2) {
            translation = v3()
        }.next("stay", delay) {
        }.next("hide", .2) {
            translation = v3(x = 200.0)
            after { Box -= this@hbox }
        }
    }

}