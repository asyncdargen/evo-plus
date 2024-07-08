package ru.dargen.evoplus.features.stats.level

import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.state.hbar
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.stats.info.StatisticData
import ru.dargen.evoplus.util.format.color
import ru.dargen.evoplus.util.format.format
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.render.alpha
import kotlin.math.min

object LevelWidget : WidgetBase {

    val ProgressBar = hbar {
        size = v3(125.0, 1.0)

        align = Relative.RightBottom
        origin = Relative.RightTop

        interpolationTime = .51

        backgroundColor = Colors.Gray
        progressColor = Colors.Green.alpha(.8)
    }
    val Text = text("???") { isShadowed = true }

    val MainBox = hbox {
        space = 1.0
        indent = v3()

        +item(itemStack(Items.EXPERIENCE_BOTTLE)) {
            scale = v3(1.2, 1.2, 1.2)
        }
        +Text
    }

    override val node = vbox {
        space = 1.0
        indent = v3()

        +MainBox
        +ProgressBar
    }

    fun update(statistic: StatisticData) {
        if (statistic.nextLevel.isMaxLevel) {
            Text.text = "§aMAX"
            ProgressBar.progress = .0
        }
        else {
            val blocks = statistic.blocks
            val money = statistic.money
            val nextBlocks = statistic.nextLevel.blocks
            val nextMoney = statistic.nextLevel.money

            Text.lines = listOf(
                "Блоки: ${(blocks >= nextBlocks).color}$blocks/$nextBlocks",
                "Деньги: ${(money >= nextMoney).color}${money.format()}/${nextMoney.format()}"
            )
            ProgressBar.progress = (min(blocks / nextBlocks.toDouble(), 1.0) + min(money / nextMoney, 1.0)) / 2.0
        }
    }

    override fun Node.prepare() {
        origin = Relative.RightCenter
        align = v3(.99, .40)
    }

}