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
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.Bosses
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ComparedBosses
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.fromJson
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.toJson

object BossFeature : Feature("boss", "Боссы", Items.DIAMOND_SWORD) {

    private val BossCapturePattern = "^Босс (.*) захвачен кланом (.*)!\$".toRegex()

    val BossDamageText = text("???? [??]: ??\uE35E") { isShadowed = true }
    val BossDamageWidget by widgets.widget("Урон по боссу", "boss-damage") {
        origin = Relative.CenterBottom
        align = v3(.58, .9)
        +BossDamageText
    }

    val NotifyCapture by settings.boolean("Уведомление о захватах боссов", true)
    val FastTeleport by settings.boolean("Телепорт к ближайшему боссу", true)

    init {
        FastBossTeleport.on { if (FastTeleport) { sendCommand("boss ${ComparedBosses.first().key.level}") } }

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