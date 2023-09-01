package ru.dargen.evoplus.feature.type.chat

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.InputNode
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.input
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.settings.*
import ru.dargen.evoplus.feature.type.FishingFeature.settings
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

class ColorInputSetting(
    id: String, name: String
) : BooleanSetting(id, name, false) {
    val inputs = mutableListOf<InputNode>()

    val colors = buildList {
        repeat(2) {
            add(settings.setting(StringSetting("chatcolor_$it", "Градация цвета", "ffffff")))
        }
    }.onEach {
        inputs.add(
            input {
                maxLength = 6
                strictSymbols()
                filter { "[a-fA-F0-9]".toRegex().matches(it.toString()) }
                on { it.toIntOrNull(16)?.run { text.color = Color(this) } }
            }
        )
    }

    final var mirroring = settings.setting(BooleanSetting("colorMirroring", "Отзеркаливание цвета", false))

    private val mirrorButton = mirroring.run {
        fun Boolean.stringify() = if (this) "Зеркальность" else "Градация"
        button(value.stringify()) {
            on {
                value = !value
                label.text = value.stringify()
            }
        }
    }

    private val resetButton = button("Сбросить") {
        on { colors.forEach { it.value = "ffffff" } }
    }

    private val confirmButton = button("Применить") {
        on {
            colors.forEachIndexed { index, color ->
                color.value = inputs[index].let {
                    it.content.ifBlank { it.prompt.text }
                }
            }
        }
    }

    override val settingSection: Node
        get() = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 55.0)

            +text(name) {
                translation = v3(6.6, 15.0)
                origin = Relative.LeftCenter
            }

            +settingElement.apply {
                translation = v3(-5.0, 15.0)
                align = Relative.RightTop
                origin = Relative.RightCenter
            }

            +hbox {
                align = Relative.LeftBottom
                origin = Relative.LeftCenter
                translation = v3(y = -15.0)
                inputs.forEachIndexed { index, inputNode ->
                    +inputNode.apply {
                        colors[index].apply {
                            content = value
                            prompt.text = value
                            inputHandler(value)
                        }
                    }
                }
                +mirrorButton
            }

            +hbox {
                align = Relative.RightBottom
                origin = Relative.RightCenter
                translation = v3(y = -15.0)
                +resetButton
                +confirmButton
            }
        }
}