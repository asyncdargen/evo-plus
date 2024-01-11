package ru.dargen.evoplus.features.clan

import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.format.nounEndings
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.printMessage

object WormFeature : Feature("worms", "Черви", Items.TURTLE_EGG) {

    var Worms = 0
        set(value) {
            field = value
            WormsText.text = "Червей: §6$value"
        }
    val WormsText = text { isShadowed = true }
    val WormsWidget by widgets.widget("Счётчик червей", "worms", enabled = false) {
        origin = Relative.CenterBottom
        align = v3(.5, .9)
        +WormsText
    }

    val WormNotify by settings.boolean("Уведомление о найденных червях", true)
    val WormMessage by settings.boolean("Сообщение о найденных червях")

    init {
        scheduleEvery(period = 10) {
            WorldEntities
                .filterIsInstance<ArmorStandEntity>()
                .filter { "Червь" in it.name.string }
                .apply {
                    if (Worms < size) {

                        val text = "§6Возле вас обнаружен${if (size > 1) "о" else ""} $size ${
                            size.nounEndings("червь", "червя", "червей")
                        }"
                        if (WormNotify) Notifies.showText(text)
                        if (WormMessage) printMessage(text)
                    }

                    Worms = size
                }
        }
    }
}