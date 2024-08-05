package ru.dargen.evoplus.features.clicker

import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.input.KeyEvent
import ru.dargen.evoplus.api.event.input.MouseClickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.Keybinds
import ru.dargen.evoplus.api.keybind.boundKey
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.type.clicker.ClickerMode
import ru.dargen.evoplus.feature.type.clicker.ClickerMouse
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import java.util.concurrent.TimeUnit
import kotlin.math.max

object AutoClickerFeature : Feature("clicker", "Кликер", Items.WOODEN_SWORD) {
    
    val BindEnabled by settings.boolean("Статус бинда")
    val Mode by settings.switcher("Режим работы", enumSelector<ClickerMode>())
    val Mouse by settings.switcher("Кнопка мыши", enumSelector<ClickerMouse>())
    val CPS by settings.selector("Кликов в секунду", (1..20).toSelector()) { "$it" }

    private var enabled = false
    private var remainToClick = 0
        set(value) {
            field = max(0, value)
        }

    init {
        Keybinds.AutoClicker.on {
            if (!BindEnabled || Mode !== ClickerMode.CLICK) return@on
            enabled = !enabled
        }
        
        on<KeyEvent> {
            if (key != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }
        on<MouseClickEvent> {
            if (button != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }

        scheduleEvery(0, 50, unit = TimeUnit.MILLISECONDS) {
            if (!enabled) return@scheduleEvery

            remainToClick -= 50

            if (remainToClick > 0) return@scheduleEvery

            remainToClick = 1000 / CPS
            Mouse()
        }
    }
}