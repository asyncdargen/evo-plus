package ru.dargen.evoplus.features.rune.widget

import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.rune.RuneFeature
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3

object AbilityTimerWidget : WidgetBase {

    override val node = vbox {
        space = 2.0
        indent = v3()
    }

    fun update() {
        node._children = buildList {
            if (RuneFeature.Abilities.isNotEmpty() || isWidgetEditor) add(text("§fОткат способностей:"))

            AbilityType.values
                .take(if (RuneFeature.Abilities.isEmpty() && isWidgetEditor) 3 else AbilityType.size)
                .associateWith { RuneFeature.Abilities[it.id] ?: 0L }
                .filterValues { RuneFeature.Abilities.isEmpty() && isWidgetEditor || it > currentMillis }
                .map { (type, timestamp) ->
                    val remainTime = (timestamp - currentMillis).coerceAtLeast(0L)

                    add(text(" §a${type.name}§8: §f${remainTime / 1000}") {
                        isShadowed = true

                        translation = v3(1.0, 1.0, 200.0)
                        scale = v3(.9, .9, .9)
                        origin = Relative.RightBottom
                        align = Relative.RightBottom
                    })
                }
        }.toMutableList()
    }

    override fun Node.prepare() {
        origin = Relative.CenterBottom
        align = v3(.35, 0.99)
    }
}