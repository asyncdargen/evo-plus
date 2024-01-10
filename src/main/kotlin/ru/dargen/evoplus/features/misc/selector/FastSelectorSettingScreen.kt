package ru.dargen.evoplus.features.misc.selector

import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.InputNode
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.input
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.after
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.identifier
import ru.dargen.evoplus.util.minecraft.itemOf

object FastSelectorSettingScreen {

    private fun textField(
        name: String, content: String,
        block: InputNode.() -> Unit = {},
        handler: (String) -> Unit
    ) = vbox {
        indent = v3()
        space = 1.0
        childrenRelative = .5

        +text(name)
        +input {
            prompt.text = name
            this.content = content

            on { handler(it) }

            strictSymbols()
            block()
        }
    }

    fun open(selectorItem: SelectorItem) = screen {
        val configuredItem = selectorItem.copy()

        +vbox {
            +text("Редактор кнопки") {
                align = Relative.CenterTop
                origin = Relative.CenterBottom
                scale = scale(2.0, 2.0)
            }

            childrenRelative = .5
            space = 20.0
            indent = v3()

            align = Relative.Center
            origin = Relative.Center

            +item {
                scale = scale(3.0, 3.0)
                preTransform { matrices, tickDelta -> item = selectorItem.displayItem }
            }

            +hbox {
                indent = v3()
                space = 20.0

                +textField("Название", selectorItem.name) { selectorItem.name = it }
                +textField("Команда", selectorItem.command) { selectorItem.command = it }
            }

            +hbox {
                indent = v3()
                space = 20.0

                +textField("Предмет", selectorItem.item.identifier) { selectorItem.item = itemOf(it) }
                +textField("ID модели", selectorItem.customModel.toString(),
                    { filter { it in '0'..'9' } }) { selectorItem.customModel = it.toIntOrNull() ?: 0 }
            }

            +button("Сохранить и выйти") {
                size.x = 170.0
                on {
                    selectorItem.apply {
                        configuredItem.name = name
                        configuredItem.command = command
                        configuredItem.item = item
                        configuredItem.customModel = customModel
                    }
                    close()
                }
            }

        }

        destroy { after { FastSelectorScreen.open(true) } }
    }.open()

}