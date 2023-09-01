package ru.dargen.evoplus.feature.type.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.chat.ChatSendEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.uncolored
import kotlin.math.ceil
import kotlin.math.floor


object ChatFeature : Feature("chat", "Чат", Items.PAPER) {

    private val RemindPattern = "^-{12}[\\s\\S]+-{12}\$".toRegex()

    var NoSpam by settings.boolean("Отключение спам-сообщений", false)

    val Colors = settings.setting(ColorInputSetting("colorInputs", "Градиент сообщения в чате"))

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }

        val formatters = listOf("!","@")

        on<ChatSendEvent> {
            if (!Colors.value) return@on
            val message = text

            val hasSelector = formatters.any { message.startsWith(it,true) }

            val prefix = if (hasSelector) message.take(1) else ""

            val colors = buildColorSetting(Colors.mirroring.value)

            val formattedMessage = message.replaceFirst(prefix,"").buildMessage(prefix, colors)

            text = formattedMessage
        }
    }

    private fun String.buildMessage(prefix: String, colors: List<String>): String {
        val mirroring = colors.size == 2
        return if (mirroring)
            "$prefix${colors[0]}${takeFirstHalf()}${colors[1]}${takeSecondHalf()}"
        else "$prefix${colors[0]}$this"
    }

    private fun String.takeFirstHalf() = take(ceil(length / 2.0).toInt())
    private fun String.takeSecondHalf() = drop(ceil(length / 2.0).toInt())

    private fun buildColorSetting(withMirroring: Boolean) = buildList{
        Colors.colors.map { it.value }.let {
            add("[#${it[0]}-#${it[1]}]")
            if (withMirroring) add("[#${it[1]}-#${it[0]}]")
        }
    }
}