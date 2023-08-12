package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.Client

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    var blockBreakParticles by settings.boolean(
        "break-particles", "Частицы ломания блока", true
    )
    var fullBright by settings.boolean("full-bright", "Полная яркость", false)

}