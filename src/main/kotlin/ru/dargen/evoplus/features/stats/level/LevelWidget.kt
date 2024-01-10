package ru.dargen.evoplus.features.stats.level

import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.stats.Statistic
import ru.dargen.evoplus.util.format.color
import ru.dargen.evoplus.util.format.format
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack

object LevelWidget : WidgetBase {

    val Text = text("???") { isShadowed = true }

    override val node = vbox {
        space = 1.0
        indent = v3()

        +hbox {
            space = 1.0
            indent = v3()

            +item(itemStack(Items.EXPERIENCE_BOTTLE)) {
                scale = v3(1.2, 1.2, 1.2)
            }
            +Text
        }
    }

    fun update(statistic: Statistic) {
        if (statistic.nextLevel.isMaxLevel) Text.text = "§aMAX"
        else Text.lines = listOf(
            "Блоки: ${(statistic.blocks >= statistic.nextLevel.blocks).color}${statistic.blocks}/${statistic.nextLevel.blocks}",
            "Деньги: ${(statistic.money >= statistic.nextLevel.money).color}${statistic.money.format()}/${statistic.nextLevel.money.format()}"
        )
    }

    override fun Node.prepare() {
        origin = Relative.RightCenter
        align = v3(.99, .40)
    }

}