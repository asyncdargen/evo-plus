package ru.dargen.evoplus.api.render.node.input

import net.minecraft.sound.SoundEvents
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.api.render.node.hover
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.playSound
import java.awt.Color

@KotlinOpens
class ButtonNode(label: String = "") : RectangleNode() {

    val label = +text(label) {
        align = Relative.Center
        origin = Relative.Center
    }

    var buttonColor: Color = Colors.Primary
        set(value) {
            field = value
            color = buttonColor()
        }

    var isSilent = false
    var clickHandler: ButtonNode.(mouse: Vector3) -> Unit = {}

    init {
        color = buttonColor()
        size = Vector3(100.0, 20.0)

        hover { _, _ -> color = buttonColor() }
        leftClick { mouse, state ->
            if (isHovered && state) {
                clickHandler(mouse)
                if (!isSilent) {
                    playSound(SoundEvents.UI_BUTTON_CLICK)
                }
            }
        }
    }

    protected fun buttonColor() = if (isHovered) buttonColor.darker() else buttonColor

    fun on(handler: ButtonNode.(mouse: Vector3) -> Unit = {}) = apply { clickHandler = handler }

}

fun button(label: String = "", block: ButtonNode.() -> Unit = {}) = ButtonNode(label).apply(block)

fun symbolButton(label: String, block: ButtonNode.() -> Unit = {}) = button(label) {
    size = v3(10.0, 10.0)
    block()
}