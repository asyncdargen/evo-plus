package ru.dargen.evoplus.features.goldenrush

import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.preRender
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.customModelData
import ru.dargen.evoplus.util.minecraft.printMessage

private const val GOLDEN_CRYSTAL_DATA = 271
private val goldenCrystalItem = Items.PAPER

object GoldenRushFeature :
    Feature("golden_rush", "Золотая Лихорадка", customItem(goldenCrystalItem, GOLDEN_CRYSTAL_DATA)) {

    var GoldenCrystalAround = false

    val GoldenCrystalBox = hbox {
        +item(customItem(goldenCrystalItem, GOLDEN_CRYSTAL_DATA)) {
            translation.y += 3
            scale = v3(.5, .5, .5)
        }
        +text {
            isShadowed = true
            preRender { _, _ ->
                lines = listOf(if (GoldenCrystalAround) "§a✔" else "§c❌")
            }
        }
    }
    val GoldenCrystalWidget by widgets.widget("Золотой Кристалл", "golden_crystal", false) {
        +GoldenCrystalBox
    }
    val GoldenCrystalNotify by settings.boolean("Уведомление о появлении золотого кристалла")
    val GoldenCrystalMessage by settings.boolean("Сообщение о появлении золотого кристалла", true)

    init {
        scheduleEvery(period = 10) {
            Client?.world?.entities
                ?.filterNotNull()
                ?.filterIsInstance<ArmorStandEntity>()
                ?.find { stand -> stand.armorItems.any { it.item === goldenCrystalItem && it.customModelData == GOLDEN_CRYSTAL_DATA } }
                .also {
                    val previousGoldenCrystalAround = GoldenCrystalAround
                    GoldenCrystalAround = it != null

                    if (previousGoldenCrystalAround || !GoldenCrystalAround) return@scheduleEvery

                    val text = "§eВозле вас обнаружен Золотой Кристалл"
                    if (GoldenCrystalNotify) Notifies.showText(text)
                    if (GoldenCrystalMessage) printMessage(text)
                }
        }
    }
}