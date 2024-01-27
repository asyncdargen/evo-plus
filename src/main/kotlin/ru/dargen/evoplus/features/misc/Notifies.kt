package ru.dargen.evoplus.features.misc

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.schduler.schedule
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import java.util.concurrent.TimeUnit

object Notifies : WidgetBase {

    override val node = vbox {
        align = Relative.RightTop
        origin = Relative.RightTop

        childrenRelative = 1.0

        fixChildSize = true

        indent = v3(8.0, 8.0)
//        translation = v3(-indent.x * 2)

        +hbox {
            color = Colors.TransparentBlack

            indent = v3(7.0, 7.0)
            translation = v3(-16.0)

            +text("Показательное уведомление", "Для настройки виджета") { scale = scale(1.1, 1.1) }

            this@vbox.preTransform { matrices, tickDelta ->
                if (this@hbox in this@vbox.nonComposingChildren) {
                    if (isWidgetEditor && this@vbox.children.size == 1) {
                        this@vbox.nonComposingChildren.remove(this@hbox)
                        this@hbox.enabled = true
                    }
                } else if (!isWidgetEditor || this@vbox.children.size > 1) {
                    this@vbox.ignore(this@hbox)
                    this@hbox.enabled = false
                }
            }
        }
    }

    fun showText(vararg text: String, delay: Double = 5.0, block: Node.() -> Unit = {}) = show(delay) {
        +text(*text) { scale = v3(1.1, 1.1, 1.1) }
        block()
    }

    fun show(delay: Double = 5.0, block: Node.() -> Unit = {}) = node + hbox {
        color = Colors.TransparentBlack
        scale = v3()

        fixChildSize = true

        translation = v3(x = -200 + node.parent!!.origin.x * 400.0)
        indent = v3(7.0, 7.0)

        block()

        var willHide = false

        fun hide() {
            if (isHovered) {
                willHide = true
            } else animate("state", .8, Easings.BackIn) {
                translation = v3(x = -200 + this@Notifies.node.parent!!.origin.x * 400.0)
                scale = v3()
                after { this@Notifies.node - this@hbox }
            }
        }

        click { _, _, state -> if (!isWidgetEditor && CurrentScreen !is GenericContainerScreen && isHovered && state) hide() }
        hover { _, hovered ->
            animate("scale", .2) { scale = if (hovered) v3(1.2, 1.2, 1.2) else v3(1.0, 1.0, 1.0) }
            color = if (!isWidgetEditor && hovered) Colors.TransparentBlack.darker() else Colors.TransparentBlack
        }
        hoverOut { if (willHide) hide() }

        animate("state", .8, Easings.BackOut) {
            scale = v3(1.0, 1.0, 1.0)
            translation = v3(-16.0)
        }

        schedule((delay * 1000).toInt(), TimeUnit.MILLISECONDS) { hide() }
    }

    override fun Node.prepare() {
        align = Relative.RightTop
        origin = Relative.RightTop
    }

}