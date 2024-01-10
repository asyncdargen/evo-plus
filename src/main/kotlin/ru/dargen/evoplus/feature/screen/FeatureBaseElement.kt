package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class FeatureBaseElement(val name: String, val settingElementSupplier: () -> Node) : FeatureScreenElement {

    override fun create() = rectangle {
        color = Colors.TransparentBlack
        size = v3(y = 30.0)
        +text(name) {
            translation = v3(x = 5.0)
            align = Relative.LeftCenter
            origin = Relative.LeftCenter
        }
        +settingElementSupplier().apply {
            translation = v3(x = -5.0)
            align = Relative.RightCenter
            origin = Relative.RightCenter
        }
    }


}