package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.selector.enumSelector

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    var FullBright by settings.boolean("full-bright", "Полная яркость", true)

    var NoBlockParticles by settings.boolean("break-particles", "Отключение эффектов блока", false)
    var NoFire by settings.boolean("no-fire", "Отключение огня", false)
    var NoStrikes by settings.boolean("no-strikes", "Отключение молний", false)
    var NoFalling by settings.boolean("no-falling", "Отключение падающих блоков", false)
    var NoDamageShake by settings.boolean("no-damage-shake", "Отключение покачивания камеры, при ударе", false)
    var NoHandShake by settings.boolean("no-damage-shake", "Отключение покачивания руки", false)
    var NoExcessHud by settings.boolean("no-excess-hud", "Отключение ненужных элементов HUD", true)
    var NoExpHud by settings.boolean("no-exp-hud", "Отключение отрисовки опыта и его уровня", true)
    var NoScoreboardNumbers by settings.boolean("no-scoreaboard-numbers", "Отключение нумерации скорборда", true)
    var HealthRender by settings.switcher("health-render", "Режим отображения здоровья", enumSelector<HealthRenderMode>())

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}