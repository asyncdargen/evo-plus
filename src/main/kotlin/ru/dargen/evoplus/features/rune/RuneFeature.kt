package ru.dargen.evoplus.features.rune

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.ability.AbilityTimers
import pro.diamondworld.protocol.packet.rune.ActiveRunes
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.rune.widget.AbilityTimerWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage

object RuneFeature : Feature("rune", "Руны", customItem(Items.PAPER, 445)) {

    val Abilities = concurrentHashMapOf<String, Long>()

    val ActiveRunesText = text(
        "§fНадетые руны:", " §e??? ???", " §6??? ???",
        " §6??? ???", " §a??? ???", " §a??? ???"
    ) {
        isShadowed = true
    }
    val ActiveAbilitiesWidget by widgets.widget(
        "",
        "active-abilities",
        enabled = false,
        widget = AbilityTimerWidget
    )
    val ActiveRunesWidget by widgets.widget("Надетые руны", "active-runes", enabled = false) {
        align = v3(0.25)
        origin = Relative.CenterTop

        +ActiveRunesText
    }
    var ReadyNotify by settings.boolean("Уведомление при окончании задержки способностей", true)
    var ReadyMessage by settings.boolean("Сообщение при окончании задержки способностей", true)

    val RunesBagProperties by settings.boolean("Отображение статистики сета рун (в мешке)", true)
    val RunesBagSet by settings.boolean("Отображать активный сет рун (в мешке)", true)
    val RunesSetSwitch by settings.boolean("Смена сетов рун через A-D и 1-7 (в мешке)", true)

    init {
        scheduleEvery(period = 2) {
            updateAbilities()

            AbilityTimerWidget.update()
        }

        RunesBag

        listen<ActiveRunes> { activeRunes ->
            ActiveRunesText.text = "§fНадетые руны:\n" + activeRunes.data.joinToString("\n") { " $it" }
        }

        listen<AbilityTimers> {
            it.timers.forEach { (id, timestamp) -> Abilities[id] = currentMillis + timestamp + 600 }
        }
    }

    private fun updateAbilities() {
        Abilities.forEach { (id, timestamp) ->
            val type = AbilityType.valueOf(id) ?: return@forEach
            val remainTime = timestamp - currentMillis

            if (remainTime in 0..1000) {
                if (ReadyNotify) Notifies.showText("§aСпособность \"${type.name}\" готова")
                if (ReadyMessage) printMessage("§aСпособность \"${type.name}\" готова")
                Abilities.remove(id)
            }

            if (remainTime < 0) Abilities.remove(id)
        }
    }
}
