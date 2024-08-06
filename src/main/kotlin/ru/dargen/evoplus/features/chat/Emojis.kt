package ru.dargen.evoplus.features.chat

import ru.dargen.evoplus.api.event.chat.ChatSendEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.Tips
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.util.format.readCSV
import ru.dargen.evoplus.util.format.wrap
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import java.net.URL

object Emojis {

    private val ChatClassNamePattern = "(class_408|Chat)".toRegex()

    /*emoji to data*/
    val EmojiMap =
        URL("https://docs.google.com/spreadsheets/d/12pLMaVhot71LSz2Trv7ykm90IgRaw75IBM4Mb0yQTps/export?format=csv&id=12pLMaVhot71LSz2Trv7ykm90IgRaw75IBM4Mb0yQTps")
            .openStream()
            .readCSV()
            .drop(1)
            .associate { (name, id, emoji) -> emoji to EmojiData(id, name, emoji) }

    init {
        on<ChatSendEvent> { text = replaceEmojisByKeys(text) }
        Overlay + vbox {
            align = Relative.RightBottom
            origin = Relative.RightBottom
            translation = v3(x = -2.0, y = -15.0)

            size = v3(114.0, 64.0)
            color = Colors.TransparentBlack

            indent = v3(1.5, 1.5)
            space = 1.5

            tick {
                val isChatScreen = ChatClassNamePattern.containsMatchIn(CurrentScreen?.javaClass?.name ?: "")
                enabled = isChatScreen && TextFeature.EmojiMenu
            }

            EmojiMap.toList().chunked(9).forEach {
                +hbox {
                    indent = v3()
                    space = 1.5

                    it.forEach { (emoji, data) ->
                        +rectangle {
                            size = v3(11.0, 11.0)
                            color = Colors.TransparentBlack
                            postRender { matrices, _ ->
                                if (isHovered) Tips.draw(matrices, data.name)
                            }
                            hover { _, state ->
                                color = if (isHovered && state) Colors.TransparentWhite else Colors.TransparentBlack
                            }
                            click { _, _, state ->
                                if (isHovered && state) {
                                    emoji.forEach { CurrentScreen?.charTyped(it, 0) }
                                    true
                                } else false
                            }
                            +text(emoji) {
                                translation = v3(-1.0)
                                origin = Relative.Center
                                align = Relative.Center
                            }
                        }
                    }
                }
            }
        }
    }

    fun lookupEmojiKey(emoji: String) = EmojiMap[emoji.lowercase()]?.key

    fun replaceEmojisByKeys(text: String) = text.map { lookupEmojiKey(it.toString()) ?: it.toString() }.joinToString("")

    data class EmojiData(val id: String, val name: String, val emoji: String, val key: String = id.wrap(":"))

}