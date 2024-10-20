package ru.dargen.evoplus.features.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.ReplacerParser
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.chat.ChatSendEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.StringRenderEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.chat.market.MarketChatTimerWidget
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.uncolored
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.ceil

object TextFeature : Feature("text", "Текст", Items.WRITABLE_BOOK) {
    
    val MarketChatTimer by widgets.widget("Таймер торгового чата", "market-chat-timer", widget = MarketChatTimerWidget, enabled = false)
    val MarketChatTimerDelay by settings.selector("Задержка торгового чата", (3..5).toSelector()) { "$it мин." }
    val NoSpam by settings.boolean("Отключение спам-сообщений")
    val CopyMessages by settings.boolean("Копировать сообщение из чата (ПКМ)", true)
    val EmojiMenu by settings.boolean("Меню эмодзи", true)
    val ReplaceUniqueUsers by settings.boolean("Заменять ники уникальных пользователей EvoPlus", true)
    val ColorInputs = settings.colorInput("Градиент сообщения в чате", id = "gradient")

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }

        val formatters = listOf("!", "@")

        on<ChatSendEvent> {
            if (!ColorInputs.value || !EvoPlusProtocol.isOnPrisonEvo()) return@on

            val message = text
            val hasSelector = formatters.any { message.startsWith(it, true) }
            val prefix = if (hasSelector) message.take(1) else ""
            val colors = buildColorSetting(ColorInputs.value)
            val formattedMessage = message.replace(prefix, "").buildMessage(prefix, colors)

            text = formattedMessage
        }
        
        on<ChatSendEvent> {
            if (!text.startsWith("$") || !EvoPlusProtocol.isOnPrisonEvo() || MarketChatTimerWidget.RemainingTime >= currentMillis) return@on
            
            val isExceededLimit = text.length - 1 >= 256
            val timerMultiplier = if (isExceededLimit) 2 else 1
            MarketChatTimerWidget.RemainingTime = currentMillis + MarketChatTimerDelay * 60 * 1000 * timerMultiplier
        }

        on<StringRenderEvent> {
            if (!ReplaceUniqueUsers) return@on

            text = text?.let(ReplacerParser::replace)

//            this.text = Replacer
//                .mapKeys { it.key.cast<String>() }
//                .mapValues { it.value.cast<String>() }
//                .filterKeys { it in text }
//                .entries
//                .fold(text) { currentText, (key, value) -> currentText.replace(key, value).replace("%text%", key) }
        }
    }

    private fun String.takeFirstHalf() = take(ceil(length / 2.0).toInt())
    private fun String.takeSecondHalf() = drop(ceil(length / 2.0).toInt())

    private fun String.buildMessage(prefix: String, colors: List<String>): String {
        val mirroring = colors.size == 2
        return if (mirroring) "$prefix${colors[0]}${takeFirstHalf()}${colors[1]}${takeSecondHalf()}"
        else "$prefix${colors[0]}$this"
    }

    private fun buildColorSetting(withMirroring: Boolean) = buildList{
        ColorInputs.inputs.map { it.content }.let {
            add("[#${it[0]}-#${it[1]}]")
            if (withMirroring) add("[#${it[1]}-#${it[0]}]")
        }
    }
}
