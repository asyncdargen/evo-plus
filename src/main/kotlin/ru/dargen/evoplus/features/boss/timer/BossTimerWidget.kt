package ru.dargen.evoplus.features.boss.timer

import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendCommand

object BossTimerWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._children = BossTimerFeature.ComparedBosses
            .take(BossTimerFeature.BossesCount)
            .associate { (key, value) -> key to (value - currentMillis) }
            .ifEmpty { if (isWidgetEditor) BossType.values.take(5).associateWith { 2000L } else emptyMap() }
            .map { (type, remaining) ->
                hbox {
                    space = 1.0
                    indent = v3()

                    +item(type.displayItem) { scale = scale(.7, .7) }
                    +text("${type.displayName}§8: §f${remaining.asTextTime}") { isShadowed = true }

                    leftClick { _, state ->
                        if (isHovered && state && !isWidgetEditor && BossTimerFeature.WidgetTeleport) {
                            sendCommand("boss ${type.level}")
                        }
                    }
                    recompose()
                }
            }.toMutableList()
    }

}