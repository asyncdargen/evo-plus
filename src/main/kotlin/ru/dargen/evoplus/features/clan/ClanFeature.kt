package ru.dargen.evoplus.features.clan

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.minecraft.asText
import ru.dargen.evoplus.util.minecraft.displayName
import ru.dargen.evoplus.util.minecraft.lore
import ru.dargen.evoplus.util.minecraft.uncolored

object ClanFeature : Feature("clan", "Клан", Items.SHIELD) {

    private val BossCapturePattern =
        "\\[Клан] Клан (\\S+) начал захват вашего босса ([\\s\\S]+)\\. Защитите его\\.".toRegex()

    val BossCaptureNotify by settings.boolean("Уведомление о захвате вашего босса", true)
    val InlineMenuClanScores by settings.boolean("Отображать базовое К.О. боссов для захвата в меню", true)

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (BossCaptureNotify) BossCapturePattern.find(text)?.run {
                val clan = groupValues[1]
                val bossName = groupValues[2]
                val bossType = BossType.valueOfName(bossName) ?: return@run

                BossTimerFeature.notify(
                    bossType,
                    "Клан §6$clan§f пытается захватить",
                    "вашего босса ${bossType.displayName}"
                )
            }
        }

        on<InventoryFillEvent> {
            if (InlineMenuClanScores && BossFeature.BossMenuPattern.containsMatchIn(openEvent?.nameString ?: "")) {
                contents.forEach {
                    val type = BossType.valueOfName(it.displayName?.string ?: return@forEach) ?: return@forEach
                    if (it.lore.none { it.string.contains("Базовое К.О. для захвата") }) {
                        it.lore = (listOf(
                            it.lore.first(),
                            "§fБазовое К.О. для захвата: §e${type.capturePoints}".asText()
                        ) + it.lore.drop(1))
                    }
                }
            }
        }

    }

}