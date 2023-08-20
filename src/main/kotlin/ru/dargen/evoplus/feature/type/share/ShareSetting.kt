package ru.dargen.evoplus.feature.type.share

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.input
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.settings.BooleanSetting
import ru.dargen.evoplus.util.PasteApi
import ru.dargen.evoplus.util.concurrent.async
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendCommand

class ShareSetting(
    id: String, name: String,
    val encoder: (nick: String) -> String,
    val decoder: (nick: String, data: String) -> Unit
) : BooleanSetting(id, name, true) {

    override val settingSection: Node
        get() = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 55.0)
            +text(name) {
                translation = v3(5.0, 15.0)
                origin = Relative.LeftCenter
            }
            +settingElement.apply {
                translation = v3(-5.0, 15.0)
                align = Relative.RightTop
                origin = Relative.RightCenter
            }
            val input = +input {
                prompt.text = "Введите ник"
                maxLength = 16
                strictSymbols()
                filter { "[a-zA-Z0-9_]".toRegex().matches(it.toString()) }

                translation = v3(5.0, -15.0)
                align = Relative.LeftBottom
                origin = Relative.LeftCenter
            }
            +button("Поделиться") {
                translation = v3(-5.0, -15.0)
                align = Relative.RightBottom
                origin = Relative.RightCenter
                on {
                    if (input.length !in 3..16) {
                        input.animate("warn", .2) {
                            input.color = Colors.Negative
                            next("warn", .05) {
                                input.color = Colors.Second
                            }
                        }
                    } else {
                        val nick = input.content

                        async {
                            val key = PasteApi.paste(encoder(nick))
                            sendCommand("m $nick evoplus:$id:$key")

                            input.clear()
                            input.animate("warn", .2) {
                                input.color = Colors.Positive
                                next("warn", .05) {
                                    input.color = Colors.Second
                                }
                            }
                        }
                    }
                }
            }
        }


}