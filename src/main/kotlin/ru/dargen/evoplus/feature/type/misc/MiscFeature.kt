package ru.dargen.evoplus.feature.type.misc

import net.minecraft.item.Items
import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.event.PlayerChangeServerEvent
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.evo.EvoQuitEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.concurrent.after
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.stream.Collectors


object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    var CurrentServer = "?????-???"
    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()

    var AutoSprint by settings.boolean("auto-sprint", "Авто-спринт", true) on {
        if (!it) Player?.isSprinting = false
    }
    val AutoThanks by settings.boolean("auto-thanks", "Авто /thx", true)
    var FastSelector by settings.boolean("fast-selector", "Меню быстрого доступа", true)
    var ShowServerInTab by settings.boolean("no-spam", "Показывать айди сервера в табе", true)

    var CaseNotify by settings.boolean("case-notify", "Уведомления о кейсах", true)
    var LuckyBlockNotify by settings.boolean("lucky-block-notify", "Уведомления о лаки-блоках", true)
    var CollectionNotify by settings.boolean("collection-notify", "Уведомления о коллекционках", true)
    var NoSpam by settings.boolean("no-spam", "Отключение спам-сообщений", false)

    init {
        on<PostTickEvent> {
            Player?.apply { if (forwardSpeed > 0) isSprinting = true }
        }
        on<EvoJoinEvent> { after(20000) { thx() } }
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (text == "В данный момент нет активных бустеров, либо вы уже поблагодарили игроков за них") cancel()

            if (BoosterMessagePattern.containsMatchIn(text)) thx()
            if (NoSpam && text.startsWith("Игроку")) cancel()
            if (text.startsWith("Вы нашли")) {
                if (CaseNotify && text.contains("кейс")) Notifies.showText("§6$text")
                if (CollectionNotify && text.contains("коллекционный предмет")) Notifies.showText("§a$text")
                if (LuckyBlockNotify && text.contains("лаки-блок")) Notifies.showText("§e$text")
            }
        }

        on<PlayerChangeServerEvent> {
            sendCommand("modinfo server")
        }
        on<ChatReceiveEvent> {
            try {
                val modInfoServer = encodeColoredText(message) ?: return@on
                if (!modInfoServer.startsWith("[MODINFO-SERVER]")) return@on
                cancel()

                val pastServer = CurrentServer
                CurrentServer = modInfoServer.split("\\s".toRegex())[1]

                if ("PRISONEVO" in CurrentServer) EventBus.fire(EvoJoinEvent)
                else if ("PRISONEVO" in pastServer) EventBus.fire(EvoQuitEvent)
            } catch (ignored: Throwable) {
            }
        }

        FastSelectorScreen
    }

    fun thx() {
        if (AutoThanks) {
            sendCommand("thx")
        }
    }
}

private val ENCODED_COLOR_ALTERNATIVES: List<Int> = listOf(
    0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 16733695
)

private fun encodeColoredText(text: Text): String? {
    var color = text.style?.color
    var buffer: MutableList<String> =
        mutableListOf(if (color == null) "" else java.lang.String.valueOf(ENCODED_COLOR_ALTERNATIVES.indexOf(color.rgb)))
    for (sibling in text.siblings) {
        color = sibling.style.color
        if (color == null || !ENCODED_COLOR_ALTERNATIVES.contains(color.rgb)) continue
        buffer.add(
            if (ENCODED_COLOR_ALTERNATIVES.indexOf(color.rgb) > 9) " " else ENCODED_COLOR_ALTERNATIVES.indexOf(
                color.rgb
            ).toString()
        )
    }
    buffer = if (java.lang.String.join("", buffer).isEmpty()) mutableListOf(
        text.string.replace("[ §]", "").replace("d", " ")
    ) else mutableListOf(
        *java.lang.String.join("", buffer).replace("-1".toRegex(), "").split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray())
    return buffer.stream()
        .filter { num: String -> num.isNotEmpty() }
        .map { number: String ->
            number.toInt()
                .toChar().toString()
        }
        .collect(Collectors.joining())
}


