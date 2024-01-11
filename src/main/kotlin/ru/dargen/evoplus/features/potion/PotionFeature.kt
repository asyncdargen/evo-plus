package ru.dargen.evoplus.features.potion

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.potion.PotionData
import ru.dargen.evoplus.api.event.inventory.InventoryClickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.potion.timer.PotionTimerWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector

object PotionFeature : Feature("potion", "Зелья", customItem(Items.POTION, 3)) {

    val PotionTimers = mutableMapOf<Int, PotionState>()
    val ComparedPotionsTimers
        get() = PotionTimers
            .mapKeys { PotionType.byOrdinal(it.key)!! }
            .asSequence()
            .sortedBy { it.value.endTime }

    val TimerWidget by widgets.widget("Зелья", "potions-timer", enabled = false, widget = PotionTimerWidget)

    val RecipeText = text("Закрепите рецепт нажатием ПКМ в меню")
    val RecipeWidget by widgets.widget("Рецепт", "recipe", enabled = false) {
        align = Relative.LeftCenter
        origin = Relative.LeftCenter

        +RecipeText
    }

    val PotionsCount by settings.selector("Кол-во отображаемых зелий", (0..15).toSelector(-1))
    val EnabledNotify by settings.boolean("Уведомление об окончании", true)
    val EnabledMessage by settings.boolean("Сообщение об окончании")
    val EnabledPotionsInTab by settings.boolean("Отображать сведения в табе", true)

    init {
        on<InventoryClickEvent> {
            if (!RecipeWidget.enabled || button != 1) return@on

            val screen = CurrentScreen as? GenericContainerScreen ?: return@on
            val title = screen.title.string.uncolored()

            if (title != "Список зелий") return@on

            val itemStack = CurrentScreenHandler?.getSlot(slot)?.stack ?: return@on

            val stringifyLore = itemStack.lore.map { it.string }

            if (stringifyLore.none { "Рецепт" in it }) return@on

            stringifyLore
                .dropWhile { "Рецепт" !in it }
                .dropLastWhile { "Ваш уровень мастерства" !in it }
                .drop(1)
                .dropLast(2)
                .also { RecipeText.lines = listOf("§a${itemStack.name.string}:", *it.toTypedArray()) }
        }

        listen<PotionData> { potionData ->
            PotionTimers.putAll(potionData.data
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