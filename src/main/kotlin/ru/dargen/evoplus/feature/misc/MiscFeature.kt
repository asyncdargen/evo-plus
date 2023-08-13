package ru.dargen.evoplus.feature.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.sendCommand
import ru.dargen.evoplus.util.uncolored

object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()

    val AutoThanks by settings.boolean("auto-settings", "Авто /thx", true)
    var NoShardMessage by settings.boolean("no-shard-message", "Отключение сообщений о шардах", true)
    var FastSelector by settings.boolean("fast-selector", "Меню быстрого доступа", true)

    init {
        on<EvoJoinEvent> { thx() }
        on<ChatReceiveEvent> {
            val text = text.uncolored()
            if (text == "В данный момент нет активных бустеров, либо вы уже поблагодарили игроков за них") {
                cancel()
            }

            if (BoosterMessagePattern.containsMatchIn(text)) thx()
            if (NoShardMessage && text == "Вы нашли шард!") cancel()
        }
        FastSelectorScreen
    }

    fun thx() {
        if (AutoThanks) {
            sendCommand("thx")
        }
    }
}

