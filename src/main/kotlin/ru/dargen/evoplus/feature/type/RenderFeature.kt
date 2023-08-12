package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    var FullBright by settings.boolean("full-bright", "Полная яркость", true)

    var NoBlockParticles by settings.boolean("break-particles", "Отключение эффектов блока", false)
    var NoFire by settings.boolean("no-fire", "Отключение огня", false)
    var NoStrikes by settings.boolean("no-strikes", "Отключение молний", false)
    var NoDamageShake by settings.boolean("no-damage-shake", "Отключение покачивания камеры", false)

}