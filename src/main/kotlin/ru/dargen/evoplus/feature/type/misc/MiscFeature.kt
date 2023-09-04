package ru.dargen.evoplus.feature.type.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.network.ChangeServerEvent
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.evo.EvoQuitEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.concurrent.after
import ru.dargen.evoplus.util.diamondworld.ColorMessageDecoder
import ru.dargen.evoplus.util.log
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit


object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    var CurrentServer = "?????-???"

    private val ModInfoServerPattern = "\\[MODINFO-SERVER] ([\\w\\s]+-[\\w\\s]+)".toRegex()
    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()

    var AutoSprint by settings.boolean("Авто-спринт", true) on {
        if (!it) Player?.isSprinting = false
    }
    var AutoThanks by settings.boolean("Авто /thx", true)
    var FastSelector by settings.boolean("Fast селектор (R)", true)
    var FastInteraction by settings.boolean("Действия с игроком в fast селекторе", true)
    var ShowServerInTab by settings.boolean("Показывать текущий сервер в табе", true)

    var CaseNotify by settings.boolean("Уведомления о кейсах", true)
    var LuckyBlockNotify by settings.boolean("Уведомления о лаки-блоках", true)
    var CollectionNotify by settings.boolean("Уведомления о коллекционках", true)

    init {
        on<PostTickEvent> {
            Player?.apply { if (AutoSprint && forwardSpeed > 0) isSprinting = true }
        }
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (text == "В данный момент нет активных бустеров, либо вы уже поблагодарили игроков за них") cancel()

            if (BoosterMessagePattern.containsMatchIn(text)) thx()
            if (text.startsWith("Вы нашли")) {
                if (CaseNotify && text.contains("кейс")) Notifies.showText("§6$text")
                if (CollectionNotify && text.contains("коллекционный предмет")) Notifies.showText("§a$text")
                if (LuckyBlockNotify && text.contains("лаки-блок")) Notifies.showText("§e$text")
            }
        }

        on<ChangeServerEvent> {
            sendCommand("modinfo server")
        }
        on<EvoJoinEvent> { after(5, TimeUnit.SECONDS) { thx() } }
        on<ChatReceiveEvent> {
            runCatching {
                val modInfoServer = ColorMessageDecoder.decode(message)
                ModInfoServerPattern.find(modInfoServer)?.apply {
                    val pastServer = CurrentServer
                    CurrentServer = groupValues[1]

                    if ("PRISONEVO" in CurrentServer) EventBus.fire(EvoJoinEvent)
                    else if ("PRISONEVO" in pastServer) EventBus.fire(EvoQuitEvent)

                    cancel()
                }
            }.exceptionOrNull()?.log("Error while fetching server")
        }

        FastSelectorScreen
    }

    fun thx() {
        if (AutoThanks) {
            sendCommand("thx")
        }
    }
}
