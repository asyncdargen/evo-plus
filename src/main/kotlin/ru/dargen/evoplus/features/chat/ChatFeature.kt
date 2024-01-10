package ru.dargen.evoplus.features.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.uncolored


object ChatFeature : Feature("chat", "Чат", Items.WRITABLE_BOOK) {

    var NoSpam by settings.boolean("Отключение спам-сообщений")
    var EmojiMenu by settings.boolean("Меню эмодзи", true)

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }
    }

}