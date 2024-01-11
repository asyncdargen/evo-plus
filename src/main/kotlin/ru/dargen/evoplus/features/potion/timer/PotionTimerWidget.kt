package ru.dargen.evoplus.features.potion.timer

import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.potion.PotionFeature
import ru.dargen.evoplus.features.potion.PotionState
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.v3

object PotionTimerWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._children = PotionFeature.ComparedPotionsTimers
            .take(PotionFeature.PotionsCount)
            .associate { it.key to it.value }
            .ifEmpty {
                if (isWidgetEditor) PotionType.values.take(5).associateWith { PotionState(55, 2000) } else emptyMap()
            }
            .map { (potionType, potionState) ->
                val (quality, endTime) = potionState
                val remainTime = endTime - currentMillis

                hbox {
                    space = 1.0
                    indent = v3()

                    +item(potionType.displayItem) {
                        scale = v3(.7, .7, .7)
                    }
                    +text {
                        isShadowed = true

                        lines = listOf("${potionType.displayName} ($quality%)§8:§f ${remainTime.asShortTextTime}")
                    }
                    recompose()
                }
            }.toMutableList()
    }
}