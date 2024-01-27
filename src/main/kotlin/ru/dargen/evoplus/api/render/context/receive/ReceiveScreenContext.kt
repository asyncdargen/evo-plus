package ru.dargen.evoplus.api.render.context.receive

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.scroll.vScrollView
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.util.math.v3

class ReceiveScreenContext (id: String, title: String) : ScreenContext(id, title) {

    val selector = vScrollView {
        box.color = Colors.TransparentBlack
    }

    val buttons = hbox {
        indent = v3(2.0, 2.0)
    }

    init {
        +vbox {
            align = Relative.Center
            origin = Relative.Center

            indent = v3(2.0, 2.0)
            space = 1.0

            tick {
                selector.size = v3(buttons.size.x, Overlay.ScaledResolution.y * .5)
            }

            +selector
            +buttons
        }
    }
}

inline fun receiveScreen(id: String = "", title: String = "", block: ReceiveScreenContext.() -> Unit) =
    ReceiveScreenContext(id, title).apply(block)