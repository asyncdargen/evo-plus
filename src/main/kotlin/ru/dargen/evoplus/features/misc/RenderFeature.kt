package ru.dargen.evoplus.features.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.render.HealthBars
import ru.dargen.evoplus.util.selector.enumSelector

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    val FullBright by settings.boolean("Полная яркость", true)

    val NoBlockParticles by settings.boolean("Отключение эффектов блока")
    val NoFire by settings.boolean("Отключение огня")
    val NoStrikes by settings.boolean("Отключение молний")
    val NoFalling by settings.boolean("Отключение падающих блоков")
    val NoDamageShake by settings.boolean("Отключение покачивания камеры, при ударе")
    val NoHandShake by settings.boolean("Отключение покачивания руки")
    val NoExcessHud by settings.boolean("Отключение ненужных элементов HUD", true)
    val NoExpHud by settings.boolean("Отключение отрисовки опыта и его уровня", true)
    val NoScoreboardNumbers by settings.boolean("Отключение нумерации скорборда", true)
    val HealthBarsRender by settings.boolean("Отображать здоровья игроков", true) on(HealthBars::updateRender)
    val HealthCountRender by settings.boolean("Отображать единицы здоровья игроков", true)
    val HealthRender by settings.switcher("Режим отображения здоровья", enumSelector<HealthRenderMode>())

    init {
        HealthBars
    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}