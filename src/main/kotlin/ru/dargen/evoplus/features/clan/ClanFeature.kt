package ru.dargen.evoplus.features.clan

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.clan.ClanInfo
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.features.stats.info.holder.ClanHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.format.fix
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
        listen<ClanInfo> { ClanHolder.accept(it.data) }

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
                    if (it.lore.none { it.string.contains("Очков для захвата") }) {
                        val capturePoints = type.capturePoints
                        val additionalPointsMultiplier = ClanHolder.Bosses.size * .03
                        val additionalPoints = additionalPointsMultiplier * capturePoints

                        val baseClanScoreText = "§fОчков для захвата: §e${(capturePoints + additionalPoints).toInt()}${
                            if (additionalPointsMultiplier > 0) " §c${(additionalPointsMultiplier * 100).fix()}"
                            else ""
                        } §8($capturePoints${if (additionalPointsMultiplier > 0) " * " + (additionalPointsMultiplier * 100).fix() else ""})"

                        it.lore = (listOf(
                            it.lore.first(),
                            baseClanScoreText.asText()
                        ) + it.lore.drop(1))
                    }
                }
            }
        }

    }

}