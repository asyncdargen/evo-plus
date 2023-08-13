package ru.dargen.evoplus.feature.misc

import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.OverlayContext
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

object Notifies {

    private val DefaultBackgroundColor = Color(0, 0, 0, 75)
    private val BackgroundColor = Color(177, 177, 177, 50)

    private val Box = OverlayContext + vbox {
        align = Relative.RightTop
        origin = Relative.RightTop

        fixChildSize = true

        indent = v3(8.0, 8.0, .0)
    }

    fun show(delay: Double = 5.0, block: Node.() -> Unit = {}) =
        Box + hbox {
            color = DefaultBackgroundColor

            align = Relative.RightTop
            origin = Relative.RightTop
            indent = v3(7.0, 5.0, .0)

            fixChildSize = true

            block(this)
            click { _, _, state ->
                if (isHovered && state) animate("click-hide", 1.0) {
                    translation = v3(y = -500.0)
                    after { Box -= this@hbox }
                }
            }
            hover { _, hovered -> color = if (hovered) BackgroundColor.brighter() else DefaultBackgroundColor }

            animate("show", 1.75) {
                translation = v3(x = -1.0)
            }.next("stay", delay) {
            }.next("hide", 1.3) {
                translation = v3(y = -500.0)
                after { Box -= this@hbox }
            }
        }

}