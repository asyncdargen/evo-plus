package ru.dargen.evoplus.feature.type.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored

object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()

    val AutoSprint by settings.boolean("auto-sprint", "Авто-спринт", true) on {
        if (!it) Player?.isSprinting = false
    }
    val AutoThanks by settings.boolean("auto-thanks", "Авто /thx", true)
    var FastSelector by settings.boolean("fast-selector", "Меню быстрого доступа", true)

    var CaseNotify by settings.boolean("case-notify", "Уведомления о кейсах", true)
    var LuckyBlockNotify by settings.boolean("lucky-block-notify", "Уведомления о лаки-блоках", true)
    var CollectionNotify by settings.boolean("collection-notify", "Уведомления о коллекционках", true)
    var NoSpam by settings.boolean("no-spam", "Отключение спам-сообщений", false)

    init {
        on<PostTickEvent> {
            Player?.apply { if (forwardSpeed > 0) isSprinting = true }
        }
        on<EvoJoinEvent> { thx() }
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
        FastSelectorScreen
    }

    fun thx() {
        if (AutoThanks) {
            sendCommand("thx")
        }
    }
}

