package ru.dargen.evoplus.features.alchemy

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import ru.dargen.evoplus.api.event.bossbar.BossBarRenderEvent
import ru.dargen.evoplus.api.event.inventory.InventoryClickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.Overlay.unaryMinus
import ru.dargen.evoplus.api.render.context.Overlay.unaryPlus
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.schedule
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.alchemy.recipe.PotionRecipe
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector
import java.util.concurrent.TimeUnit

object AlchemyFeature : Feature("alchemy", "Алхимия", Items.BREWING_STAND) {

    private val alchemyTimePattern = "Время: ([.\\d]+)с".toRegex()

    var PotionRecipe: PotionRecipe? = null
    var isAlertRendered = false

    val RecipeText = text("Закрепите рецепт нажатием ПКМ в меню")
    val RecipeWidget by widgets.widget("Рецепт", "recipe", enabled = false) {
        align = Relative.LeftCenter
        origin = Relative.LeftCenter

        +RecipeText
    }
    val BrewingAlertDelay by settings.selector(
        "Задержка перед оповещением при варке по закреп. рецепту (мс)",
        (100..2000).toSelector()
    )
    val BrewingAlertDisappears by settings.selector(
        "Исчезновение оповещения при варке по закреп. рецепту (мс)",
        (250..1000).toSelector()
    )
    val SoundAlert by settings.boolean("Звук оповещения")

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
                .also {
                    RecipeText.lines = listOf("§a${itemStack.name.string}:", *it.toTypedArray())
                    PotionRecipe = PotionRecipe(it.drop(1).dropLast(1))
                }
        }

        on<BossBarRenderEvent> {
            if (isAlertRendered) return@on

            val title = bossBar.name.string.uncolored()
            val time = alchemyTimePattern.find(title)
                ?.groupValues
                ?.getOrNull(1)
                ?.toDoubleOrNull() ?: return@on

            val nearestAlert = PotionRecipe?.getNearestAlert(BrewingAlertDelay / 1000.0, time) ?: return@on

            isAlertRendered = true

            val text = +text("§c$nearestAlert") {
                align = Relative.CenterTop
                origin = Relative.CenterTop
                scale = v3(3.5, 3.5, 3.5)

                translation.y += 100
                if (SoundAlert) repeat (5) { Player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f) }
            }
            schedule(BrewingAlertDisappears, TimeUnit.MILLISECONDS) {
                -text
                isAlertRendered = false
            }
        }
    }
}