package ru.dargen.evoplus.feature.type.share

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.PasteApi
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.CompletableFuture

object ShareFeature : Feature("share", "Поделиться", Items.SCULK_SENSOR) {

    val OutgoingSharePattern = "^ЛС \\| Я »(?:| .) \\w+: evoplus:\\w+:\\w+\$".toRegex()
    val IncomingSharePattern = "^ЛС \\|(?:| .) (\\w+) » Я: evoplus:(\\w+):(\\w+)\$".toRegex()

    val ClanSharePattern = "^\\[Клан] (\\w+) \\[.*]: evoplus:(\\w+):(\\w+)\$".toRegex()

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (OutgoingSharePattern.containsMatchIn(text)) cancel()
            else (IncomingSharePattern.find(text) ?: ClanSharePattern.find(text))?.run {
                cancel()

                val (nick, id, key) = destructured
                if (nick == Player?.gameProfile?.name) return@run

                settings.value
                    .filterIsInstance<ShareSetting>()
                    .firstOrNull { it.id == id && it.value }
                    ?.run {
                        CompletableFuture.supplyAsync { PasteApi.copy(key)!! }.thenAccept { decoder(nick, it) }
                    }
            }
        }
    }

    fun create(
        id: String, name: String,
        encoder: (nick: String) -> String,
        decoder: (nick: String, data: String) -> Unit
    ) = settings.setting(ShareSetting(id, name, encoder, decoder))

}