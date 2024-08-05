package ru.dargen.evoplus.features.stats.combo

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
import ru.dargen.evoplus.features.stats.StatisticFeature
import ru.dargen.evoplus.util.format.color
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.render.alpha

object ComboWidget : WidgetBase {

    val ProgressBar = hbar {
        size = v3(125.0, 1.0)

        align = Relative.RightBottom
        origin = Relative.RightTop

        interpolationTime = .51

        backgroundColor = Colors.Gray
        progressColor = Colors.Green.alpha(.8)
    }
    val Text = text("????") { isShadowed = true }
    val MainBox = hbox {
        space = 1.0
        indent = v3()

        +item(itemStack(Items.GOLDEN_PICKAXE)) {
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

    fun update(data: ComboData) {
        if (data.isMaxed) {
            Text.text = "Бустер: §ax${data.booster.fix(1)}"
            ProgressBar.enabled = false
            ProgressBar.progress = .0
        } else {
            Text.lines = listOf(
                "Блоки: ${data.isCompleted.color + "${data.blocks}/${data.requiredBlocks}"}",
                "Бустер: §ax${data.booster.fix(2)} §8-> §7x${data.nextBooster.fix(2)}",
                *(if (data.isExpiring) arrayOf("§cИстекает через ${data.remain} сек.") else emptyArray())
            )
            ProgressBar.enabled = StatisticFeature.ComboProgressBarEnabled
            ProgressBar.progress = data.progress
        }
    }

    override fun Node.prepare() {
        origin = Relative.RightCenter
        align = v3(.99, .48)
    }

}