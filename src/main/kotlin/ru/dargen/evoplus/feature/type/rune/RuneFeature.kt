package ru.dargen.evoplus.feature.type.rune

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.customItem

object RuneFeature : Feature("rune", "Руны", customItem(Items.PAPER, 9)) {

    val RunesBagProperties by settings.boolean("runes-bag-properties", "Показывать суммарные статы рун в мешке", true)
    val RunesBagSet by settings.boolean("runes-bag-runes", "Показывать активный сет рун", true)
    val RunesSetSwitch by settings.boolean("runes-bag-switch", "Смена сетов рун через A-D и 1-7", true)

    init {
        RunesBag
    }

}
