package ru.dargen.evoplus.features.boss.worm

import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.node.preRender
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.format.nounEndings
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.printMessage

object WormFeature : Feature("worms", "Черви", Items.TURTLE_EGG) {

    var Worms = 0
    val WormsText = text {
        isShadowed = true
        preRender { _, _ -> lines = listOf("§fЧервей: §6$Worms") }
    }
    val WormsWidget by widgets.widget("Счётчик червей", "worms") { +WormsText }
    val WormNotify by settings.boolean("Уведомление о найденных червях", true)
    val WormMessage by settings.boolean("Сообщение о найденных червях")

    init {
        scheduleEvery(period = 10) {
            Client?.world?.entities
                ?.filterNotNull()
                ?.filterIsInstance<ArmorStandEntity>()
                ?.filter { "Червь" in it.name.string }
                ?.apply {
                    val previousWorms = Worms
                    Worms = size

                    if (previousWorms >= Worms) return@apply

                    val text = "§6Возле вас обнаружен${if (Worms > 1) "о" else ""} $Worms ${
                        Worms.nounEndings(
                            "червь",
                            "червя",
                            "червей"
                        )
                    }"
                    if (WormNotify) Notifies.showText(text)
                    if (WormMessage) printMessage(text)
                }
        }
    }
}