package ru.dargen.evoplus.feature.misc

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

    val Box = OverlayContext + vbox {
        fixChildSize = true

        indent = v3(8.0, 8.0, .0)
        space = 8.0
    }

    fun show(text: String, delay: Double = 9.0, block: Node.() -> Unit = {}) =
        Box + hbox {
            indent = v3(8.0, 8.0, .0)
            space = 8.0

            +text(text)
            +vbox(block)

            animate("show", .2) {
                enabled = true
                translation = v3(x = 200.0)
            }.next("stay", delay) {
            }.next("hide", 0.2) {
                translation = v3(x = -10.0)
                after { enabled = false; Box -= this@hbox }
            }
        }

}