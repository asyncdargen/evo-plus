package ru.dargen.evoplus.features.stats.pet

import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.stats.StatisticFeature
import ru.dargen.evoplus.util.format.format
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3

object PetInfoWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._children = StatisticFeature.ActivePets
            .ifEmpty { if (isWidgetEditor) listOf(PetInfo.getDummy(), PetInfo.getDummy()) else emptyList() }
            .map { info ->
                val type = info.type

                hbox {
                    space = 1.0
                    indent = v3()

                    +item(type.displayItem) { scale = scale(.7, .7) }
                    +text(
                        "${type.displayName} §8[§e${info.level}§8] §8(§e${info.energy.format("###")}⚡§8)"
                    ) { isShadowed = true }
                    recompose()
                }
            }.toMutableList()
    }
}