package ru.dargen.evoplus.features.share

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.input
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.async
import ru.dargen.evoplus.feature.screen.FeatureScreenElement
import ru.dargen.evoplus.feature.settings.BooleanSetting
import ru.dargen.evoplus.util.PasteApi
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendChatMessage
import ru.dargen.evoplus.util.minecraft.sendCommand

class ShareSetting(
    id: String, name: String,
    val encoder: (nick: String) -> String,
    val decoder: (nick: String, data: String) -> Unit
) : BooleanSetting(id, name, true) {

    override val settingElement = object : FeatureScreenElement {
        override fun create() = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 55.0)
            +text(name) {
                translation = v3(6.6, 15.0)
                origin = Relative.LeftCenter
            }
            +super@ShareSetting.settingElement.create().apply {
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
            +button("В клан") {
                translation = v3(-5.0, -15.0)
                align = Relative.RightBottom
                origin = Relative.RightCenter
                on {
                    async { sendChatMessage("@${generate("clan")}") }
                }
            }
            +button("Игроку") {
                translation = v3(-110.0, -15.0)
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
                            sendCommand("m $nick ${generate(nick)}")

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

    fun generate(nick: String) = "evoplus:$id:${PasteApi.paste(encoder(nick))}"

}