package ru.dargen.evoplus.features.boss

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.boss.BossDamage
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.Keybinds.FastBossTeleport
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.Bosses
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ComparedBosses
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.mixin.render.hud.BossBarHudAccessor
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.json.fromJson
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.sendClanMessage
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import ru.dargen.evoplus.util.selector.toSelector
import ru.dargen.evoplus.util.json.toJson
import java.util.concurrent.TimeUnit

object BossFeature : Feature("boss", "Боссы", Items.DIAMOND_SWORD) {

    private val BossCapturePattern = "^Босс (.*) захвачен кланом (.*)!\$".toRegex()
    private val BossHealthsPattern = "([а-яА-Я ]+)\\s\\s(\\d+)".toRegex()
    val BossMenuPattern = "[\uE910\uE911]".toRegex()

    val BossDamageText = text("???? [??]: ??\uE35E") { isShadowed = true }
    val BossDamageWidget by widgets.widget("Урон по боссу", "boss-damage") {
        origin = Relative.CenterBottom
        align = v3(.58, .9)
        +BossDamageText
    }

    val NearTeleport by settings.boolean("Телепорт к ближайшему боссу")
    val NotifyCapture by settings.boolean("Уведомление о захватах боссов", true)
    val BossLowHealthsMessage by settings.boolean("Сообщение об определённом проценте здоровья босса в клановый чат")
    val BossHealthsPercent by settings.selector("Оповещать о здоровье босса при", (5..75).toSelector()) { "$it%" }
    val BossHealthsCooldown by settings.selector(
        "Оповещать о здоровье босса раз в",
        (5..25).toSelector()
    ) { "$it сек." }

    init {
        FastBossTeleport.on {
            if (NearTeleport) sendCommand("boss ${ComparedBosses.firstOrNull { it.value > currentMillis }?.key?.level}")
        }

        listen<BossDamage> {
            val type = BossType.valueOf(it.id) ?: return@listen
            BossDamageText.text = "${type.displayName}: §c${it.count}§r\uE35E"
        }

        on<ChatReceiveEvent> {
            if (NotifyCapture) BossCapturePattern.find(text)?.run {
                val type = BossType.valueOfName(groupValues[1]) ?: return@run
                val clan = groupValues[2]

                Notifies.showText("Босс ${type.displayName}§f захвачен", "кланом $clan.")
            }
        }

        scheduleEvery(unit = TimeUnit.SECONDS) {
            if (it.executions % BossHealthsCooldown == 0) return@scheduleEvery

            Client?.inGameHud?.bossBarHud?.cast<BossBarHudAccessor>()?.bossBars?.values
                ?.filter { it.name.string.uncolored().trim().isNotEmpty() }
                ?.firstNotNullOfOrNull {
                    if (!BossLowHealthsMessage) return@scheduleEvery
                    val text = it.name.string.uncolored().trim()

                    BossHealthsPattern.find(text)?.run {
                        val percent = it.percent.toDouble() * 100.0

                        if (percent > BossHealthsPercent) return@run

                        val type = BossType.valueOfName(groupValues[1]) ?: return@run
                        val health = groupValues[2].toDoubleOrNull() ?: return@run

                        sendClanMessage(
                            "§aБосс ${type.displayName}§a имеет §c${percent.fix()}% §8(§c${health.fix()}❤§8) §aздоровья!"
                        )
                    }
                } ?: return@scheduleEvery
        }

        ShareFeature.create(
            "bosses", "Таймеры боссов",
            { toJson(Bosses.mapValues { it.value - currentMillis }) }
        ) { nick, data ->
            val shared = fromJson<Map<String, Long>>(data)
                .mapKeys { BossType.valueOf(it.key) ?: return@create }
                .mapValues { it.value + currentMillis }

            Notifies.showText(
                "§6$nick §fотправил вам боссов §7(${shared.size}).",
                "Нажмите, чтобы принять.",
                delay = 10.0
            ) {
                leftClick { _, state -> if (isHovered && state) BossReceiveScreen.open(shared) }
            }
        }
    }


}