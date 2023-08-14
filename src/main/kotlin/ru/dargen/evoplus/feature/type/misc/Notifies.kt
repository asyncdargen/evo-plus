package ru.dargen.evoplus.feature.type.misc

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.util.concurrent.after
import ru.dargen.evoplus.util.math.v3
import java.awt.Color
import java.util.concurrent.TimeUnit

object Notifies {

    private val DefaultBackgroundColor = Color(0, 0, 0, 75)
    private val BackgroundColor = Color(177, 177, 177, 50)

    private val Box = Overlay + vbox {
        align = Relative.RightTop
        origin = Relative.RightTop

        childrenRelative = 1.0
        fixChildSize = true

        indent = v3(8.0, 8.0, .0)
        translation = v3(-indent.x * 2) //fucked hbox recompose...
    }

    fun showText(vararg text: String, delay: Double = 5.0, block: Node.() -> Unit = {}) = show(delay) {
        +text(*text) { scale = v3(1.1, 1.1, 1.1) }
        block()
    }

    fun show(delay: Double = 5.0, block: Node.() -> Unit = {}) =
        Box + hbox {
            color = Colors.TransparentBlack

            scale = v3()
            translation = v3(x = 200.0)
            indent = v3(7.0, 7.0)

            fixChildSize = true
            block()

            var willHide = false

            fun hide() {
                if (isHovered) {
                    willHide = true
                } else animate("state", .8, Easings.BackIn) {
                    translation = v3(x = 200.0)
                    scale = v3()
                    after { Box - this@hbox }
                }
            }

            click { _, _, state -> if (isHovered && state) hide() }
            hover { _, hovered ->
                animate("scale", .2) {
                    scale = if (hovered) v3(1.2, 1.2, 1.2) else v3(1.0, 1.0, 1.0)
                }
                color = if (hovered) Colors.TransparentBlack.darker() else Colors.TransparentBlack
            }
            hoverOut { if (willHide) hide() }

            animate("state", .8, Easings.BackOut) {
                scale = v3(1.0, 1.0, 1.0)
                translation = v3()
            }

            after((delay * 1000).toLong(), TimeUnit.MILLISECONDS) { hide() }
        }

}