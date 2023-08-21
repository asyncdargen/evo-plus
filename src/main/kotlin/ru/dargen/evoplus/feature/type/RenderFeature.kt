package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.selector.enumSelector

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    var FullBright by settings.boolean("Полная яркость", true)

    var NoBlockParticles by settings.boolean("Отключение эффектов блока")
    var NoFire by settings.boolean("Отключение огня")
    var NoStrikes by settings.boolean("Отключение молний")
    var NoFalling by settings.boolean("Отключение падающих блоков")
    var NoDamageShake by settings.boolean("Отключение покачивания камеры, при ударе")
    var NoHandShake by settings.boolean("Отключение покачивания руки")
    var NoExcessHud by settings.boolean("Отключение ненужных элементов HUD", true)
    var NoExpHud by settings.boolean("Отключение отрисовки опыта и его уровня", true)
    var NoScoreboardNumbers by settings.boolean("Отключение нумерации скорборда", true)
    var HealthRender by settings.switcher("Режим отображения здоровья", enumSelector<HealthRenderMode>())

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}