package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.type.boss.BossFeature
import ru.dargen.evoplus.feature.type.boss.BossType
import ru.dargen.evoplus.util.minecraft.uncolored

object ClanFeature : Feature("Клан", "clan", Items.SHIELD) {

    val BossCapturePattern = "\\[Клан] Клан (\\S+) начал захват вашего босса ([\\s\\S]+)\\. Защитите его\\.".toRegex()

    val BossCaptureNotify by settings.boolean("boss-capture-notify", "Уведомление о захвате вашего босса.", true)

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (BossCaptureNotify) {
                BossCapturePattern.find(text)?.run {
                    val clan = groupValues[1]
                    val bossName = groupValues[2]
                    val bossType = BossType[bossName]!!

                    BossFeature.notify(
                        bossType,
                        "Клан §6$clan§f пытается захватить",
                        "вашего босса ${bossType.displayName}"
                    )
                }
            }
        }
    }

}