package ru.dargen.evoplus.feature.type.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.uncolored

object ChatFeature : Feature("chat", "Чат", Items.PAPER) {

    private val RemindPattern = "^-{12}[\\s\\S]+-{12}\$".toRegex()

    var NoSpam by settings.boolean("Отключение спам-сообщений", false)
//    var NoRemind by settings.boolean("no-remind", "Отключение напоминаний", false)

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
//            if (NoRemind && RemindPattern.containsMatchIn(text)) cancel()
        }
    }


}