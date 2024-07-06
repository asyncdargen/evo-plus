package ru.dargen.evoplus.features.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.ReplacerParser.Replacer
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.StringRenderEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.minecraft.uncolored


object TextFeature : Feature("text", "Текст", Items.WRITABLE_BOOK) {

    var NoSpam by settings.boolean("Отключение спам-сообщений")
    var CopyMessages by settings.boolean("Копировать сообщение из чата (ПКМ)", true)
    var EmojiMenu by settings.boolean("Меню эмодзи", true)
    var ReplaceUniqueUsers by settings.boolean("Заменять ники уникальных пользователей EvoPlus", true)

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
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
}