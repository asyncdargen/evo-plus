package ru.dargen.evoplus.features.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.ReplacerParser.Replacer
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.chat.ChatSendEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.StringRenderEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.type.ColorInputSetting
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.minecraft.uncolored
import kotlin.math.ceil


object TextFeature : Feature("text", "Текст", Items.WRITABLE_BOOK) {

    var NoSpam by settings.boolean("Отключение спам-сообщений")
    var CopyMessages by settings.boolean("Копировать сообщение из чата (ПКМ)", true)
    var EmojiMenu by settings.boolean("Меню эмодзи", true)
    var ReplaceUniqueUsers by settings.boolean("Заменять ники уникальных пользователей EvoPlus", true)
    var Colors = settings.setting(ColorInputSetting("colorInputs", "Градиент сообщения в чате"))

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }

        val formatters = listOf("!", "@")

        on<ChatSendEvent> {
            if (!Colors.value || "PRISONEVO" !in EvoPlusProtocol.Server.serverName) return@on

            val message = text
            val hasSelector = formatters.any { message.startsWith(it, true) }
            val prefix = if (hasSelector) message.take(1) else ""
            val colors = buildColorSetting(Colors.mirroring.value)
            val formattedMessage = message.replace(prefix, "").buildMessage(prefix, colors)

            text = formattedMessage
        }

        on<StringRenderEvent> {
            if (!ReplaceUniqueUsers) return@on

            val text = text ?: return@on

            this.text = Replacer
                .mapKeys { it.key.cast<String>() }
                .mapValues { it.value.cast<String>() }
                .filterKeys { it in text }
                .entries
                .fold(text) { currentText, (key, value) -> currentText.replace(key, value).replace("%text%", key) }
        }
    }

    private fun String.takeFirstHalf() = take(ceil(length / 2.0).toInt())
    private fun String.takeSecondHalf() = drop(ceil(length / 2.0).toInt())

    private fun String.buildMessage(prefix: String, colors: List<String>): String {
        val mirroring = colors.size == 2
        return if (mirroring)
            "$prefix${colors[0]}${takeFirstHalf()}${colors[1]}${takeSecondHalf()}"
        else "$prefix${colors[0]}$this"
    }

    private fun buildColorSetting(withMirroring: Boolean) = buildList{
        Colors.colors.map { it.value }.let {
            add("[#${it[0]}-#${it[1]}]")
            if (withMirroring) add("[#${it[1]}-#${it[0]}]")
        }
    }
}
