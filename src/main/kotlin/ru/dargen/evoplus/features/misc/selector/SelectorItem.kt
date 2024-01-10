package ru.dargen.evoplus.features.misc.selector

import net.minecraft.item.Item
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.node.hover
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem

data class SelectorItem(
    var item: Item, var customModel: Int,
    var command: String, var name: String
) {

    val isInteraction get() = "{player}" in name || "{player}" in command
    val displayItem get() = customItem(if (item == Items.AIR) Items.BARRIER else item, customModel)

    val screenItem
        get() = rectangle {
            size = v3(16.0, 16.0) * 3.1

            val item = +item(customItem(item, customModel)) {
                origin = Relative.Center
                align = Relative.Center

                scale = scale(2.7, 2.7)
            }

            hover { _, state ->
                animate("hover", .07) {
                    item.scale = if (state) scale(3.1, 3.1) else scale(2.7, 2.7)
                }
            }
        }

}