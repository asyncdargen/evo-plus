package ru.dargen.evoplus.features.game

import net.minecraft.client.render.DiffuseLighting
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.postTransform
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.equalCustomModel
import ru.dargen.evoplus.util.minecraft.printMessage


private val GoldenCristalItem = customItem(Items.PAPER, 271)

object GoldenRushFeature : Feature("golden-rush", "Золотая Лихорадка", Items.GOLD_INGOT) {

    var GoldenCrystalAround = false
        set(value) {
            field = value
            GoldenCrystalIndicatorText.text = if (value) "§a✔" else "§c❌"
        }
    val GoldenCrystalIndicatorText = text {
        isShadowed = true
        scale = scale(1.2, 1.2)
    }
    val GoldenCrystalWidget by widgets.widget("Золотой Кристалл", "golden-crystal", false) {
        align = v3(.95, .26)
        +hbox {
            indent = v3()
            space = 3.0

            +item(GoldenCristalItem) {
                scale = scale(.5, .5, .5)
                rotation = v3(y = 50.0)
                translation = v3(x = 12.0)

                preTransform { matrices, tickDelta -> DiffuseLighting.disableGuiDepthLighting() }
                postTransform { matrices, tickDelta -> DiffuseLighting.enableGuiDepthLighting() }
            }
            +GoldenCrystalIndicatorText
        }
    }

    val GoldenCrystalNotify by settings.boolean("Уведомление о появлении золотого кристалла")
    val GoldenCrystalMessage by settings.boolean("Сообщение о появлении золотого кристалла", true)

    init {
        scheduleEvery(period = 10) {
            WorldEntities
                .filterIsInstance<ArmorStandEntity>()
                .find { stand -> stand.armorItems.any { it.equalCustomModel(GoldenCristalItem) } }
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