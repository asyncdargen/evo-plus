package ru.dargen.evoplus.features.stats

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.combo.Combo
import pro.diamondworld.protocol.packet.combo.ComboBlocks
import pro.diamondworld.protocol.packet.game.GameEvent
import pro.diamondworld.protocol.packet.game.LevelInfo
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.stats.combo.ComboData
import ru.dargen.evoplus.features.stats.combo.ComboWidget
import ru.dargen.evoplus.features.stats.level.LevelWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack

object StatisticFeature : Feature("statistic", "Статистика", Items.PAPER) {

    val ComboCounterWidget by widgets.widget("Счетчик комбо", "combo-counter", widget = ComboWidget)
    val ComboData = ComboData()

    val LevelRequireWidget by widgets.widget("Требования на уровень", "level-require", widget = LevelWidget)
    val Statistic = Statistic()

    var BlocksCount = 0
        set(value) {
            field = value
            BlocksCounterText.text = "${Statistic.blocks - field}"
        }
    val BlocksCounterText = text("0") { isShadowed = true }
    val BlocksCounterWidget by widgets.widget("Счетчик блоков", "block-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .54)
        +hbox {
            space = .0
            indent = v3()

            +BlocksCounterText
            +item(itemStack(Items.DIAMOND_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }

    var CurrentEvent = GameEvent.EventType.NONE

    init {
        screen.baseElement("Сбросить счетчик блоков") { button("Сбросить") { on { BlocksCount = Statistic.blocks } } }

        listen<Combo> {
            ComboData.fetch(it)
            ComboWidget.update(ComboData)
        }
        listen<ComboBlocks> {
            ComboData.fetch(it)
            ComboWidget.update(ComboData)
        }

        listen<LevelInfo> {
            Statistic.fetch(it)
            LevelWidget.update(Statistic)

            if (BlocksCount == 0) BlocksCount = it.blocks
            BlocksCounterText.text = "${it.blocks - BlocksCount}"
        }
        listen<GameEvent> {
            CurrentEvent = it.type
        }
    }

}