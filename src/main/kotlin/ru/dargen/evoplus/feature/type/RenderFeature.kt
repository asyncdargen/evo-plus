package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.Client

object RenderFeature : Feature("render", "Рендер", Items.REDSTONE) {

    var blockBreakParticles by settings.boolean(
        "break-particles", "Чатицы ломания блока", false
    )
    var fullBright by settings.boolean("full-bright", "Полная яркость", true) on {
        Client?.options?.gamma?.value = if (it) 100.0 else 1.0
    }

}