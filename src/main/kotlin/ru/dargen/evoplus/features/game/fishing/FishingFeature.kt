package ru.dargen.evoplus.features.game.fishing

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import pro.diamondworld.protocol.packet.fishing.SpotNibbles
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector
import java.util.concurrent.TimeUnit
import kotlin.math.max

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val BackpackTitle = "\uE974"
    val PetExpPattern = "^Опыта дает питомцу: (\\d+)\$".toRegex()
    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val Nibbles = mutableMapOf<String, Double>()
    val HourlyQuests = mutableMapOf<Int, HourlyQuestInfoHolder>()

    val NibblesWidget by widgets.widget(
        "Клёв на территориях",
        "spot-nibbles",
        widget = SpotNibblesWidget,
        enabled = false
    )
    val FishExpWidget by widgets.widget("Счёт опыта рыбы", "fish-exp") {
        val fishExpText = +text("Опыт питомцам: 0") {
            isShadowed = true
        }

        tick {
            val currentScreen = CurrentScreen
            val isCraftingInventory = (currentScreen is InventoryScreen)
            val isBackpackInventory =
                (currentScreen is GenericContainerScreen && BackpackTitle in currentScreen.title.string)

            render = isWidgetEditor || isCraftingInventory || isBackpackInventory

            if (!isCraftingInventory && !isBackpackInventory) return@tick

            val exp = max(
                currentScreen
                    .safeCast<GenericContainerScreen>()
                    ?.screenHandler
                    ?.stacks
                    ?.findExp() ?: 0,
                Player!!.inventory.items.findExp()
            )

            fishExpText.text = "Опыт питомцам: $exp"
        }
    }
    val NormalQuestsProgressWidget by widgets.widget(
        "Прогресс заданий (Обычный мир)",
        "normal-quests-progress",
        enabled = false,
        widget = NormalProgressWidget
    )
    val NetherQuestsProgressWidget by widgets.widget(
        "Прогресс заданий (Ад)",
        "nether-quests-progress",
        enabled = false,
        widget = NetherProgressWidget
    )

    val LoreProgressTips by settings.boolean("Показывать описание при наведении", true)
    val AutoFish by settings.boolean("Автоматическая удочка", true)
    val HookDelay by settings.selector("Задержка удочки (тик = 50 мс)", (0..40).toSelector(1))
    val HigherBitingNotify by settings.boolean("Уведомления о повышенном клёве", true)

    init {
        scheduleEvery(unit = TimeUnit.SECONDS) {
            SpotNibblesWidget.update()
            NormalProgressWidget.update()
            NetherProgressWidget.update()
        }

        listen<SpotNibbles> {
            Nibbles.putAll(it.nibbles)
        }

        listen<HourlyQuestInfo> { info ->
            HourlyQuests.clear()
            HourlyQuests.putAll(info.data.mapValues { HourlyQuestInfoHolder(HourlyQuestType.byOrdinal(it.key)!!, it.value) })
        }

        var fishHookTicks = 0
        on<PostTickEvent> {
            if (AutoFish && Player?.fishHook?.isSink == true) {
                if (++fishHookTicks >= HookDelay) {
                    fishHookTicks = 0
                    Player?.fishHook?.kill()
                    InteractionManager?.interactItem(Player!!, Hand.MAIN_HAND)
                }
            } else fishHookTicks = 0
        }

        on<ChatReceiveEvent> {
            if (HigherBitingNotify) HigherBitingPattern.find(text.uncolored())?.run {
                Notifies.showText("На локации §6${groupValues[1]}", "повышенный клёв.")
            }
        }
    }

    private fun Collection<ItemStack>.findExp() = mapNotNull { item ->
        item.lore.getOrNull(2)
            ?.string
            ?.let { PetExpPattern.find(it.trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.times(item.count) }
    }.sum().takeIf { it > 0 } ?: 0
}