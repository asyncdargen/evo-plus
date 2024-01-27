package ru.dargen.evoplus.features.game.fishing

import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ShortName
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ShortTimeFormat
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.protocol.registry.FishingSpot
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.format
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendCommand
import kotlin.math.absoluteValue

object SpotNibblesWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._children = FishingFeature.Nibbles
            .mapKeys { FishingSpot.valueOf(it.key) ?: return }
            .ifEmpty { if (isWidgetEditor) FishingSpot.values.take(5).associateWith { 100.0 } else emptyMap() }
            .map { (spot, nibble) ->
                hbox {
                    space = 1.0
                    indent = v3()

                    +text("§e${spot.name} §7- §6${nibble.format("###.#")}%") { isShadowed = true }
                    recompose()
                }
            }.toMutableList()
    }

}