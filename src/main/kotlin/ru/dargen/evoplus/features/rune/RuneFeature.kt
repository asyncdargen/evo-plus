package ru.dargen.evoplus.features.rune

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.customItem

object RuneFeature : Feature("rune", "Руны", customItem(Items.PAPER, 9)) {

    val RunesBagProperties by settings.boolean("Отображение статистики сета рун", true)
    val RunesBagSet by settings.boolean("Отображать активный сет рун", true)
    val RunesSetSwitch by settings.boolean("Смена сетов рун через A-D и 1-7", true)

    init {
        RunesBag
    }

}
