package ru.dargen.evoplus.features.potion

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.potion.PotionData
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.potion.timer.PotionTimerWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage
import ru.dargen.evoplus.util.selector.toSelector

object PotionFeature : Feature("potion", "Зелья", customItem(Items.POTION, 3)) {

    val PotionTimers = mutableMapOf<Int, PotionState>()
    val ComparedPotionsTimers
        get() = PotionTimers
            .mapKeys { PotionType.byOrdinal(it.key)!! }
            .asSequence()
            .sortedBy { it.value.endTime }

    val TimerWidget by widgets.widget("Зелья", "potions-timer", enabled = false, widget = PotionTimerWidget)

    val PotionsCount by settings.selector("Кол-во отображаемых зелий", (0..15).toSelector(-1))
    val EnabledNotify by settings.boolean("Уведомление об окончании", true)
    val EnabledMessage by settings.boolean("Сообщение об окончании")
    val EnabledPotionsInTab by settings.boolean("Отображать сведения в табе", true)

    init {
        listen<PotionData> { potionData ->
            PotionTimers.putAll(potionData.data
                .filterValues { it.remained > 0 && it.quality > 0 }
                .mapValues { PotionState(it.value.quality, currentMillis + it.value.remained) }
            )
        }

        scheduleEvery(period = 10) {
            updatePotions()

            PotionTimerWidget.update()
        }
    }

    private fun updatePotions() {
        ComparedPotionsTimers.forEach { (potionType, potionState) ->
                val potionName = potionType.displayName
                val (quality, endTime) = potionState
                val remainTime = endTime - currentMillis

                if (remainTime < 0) {
                    if (EnabledNotify) Notifies.showText("$potionName ($quality%)§c закончилось")
                    if (EnabledMessage) printMessage("$potionName ($quality%)§c закончилось")

                    PotionTimers.remove(potionType.id)
                }
            }
    }
}