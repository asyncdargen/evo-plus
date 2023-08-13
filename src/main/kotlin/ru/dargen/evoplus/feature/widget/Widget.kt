package ru.dargen.evoplus.feature.widget

import com.google.gson.JsonElement
import ru.dargen.evoplus.api.render.context.OverlayContext
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.alpha
import ru.dargen.evoplus.util.asDouble
import ru.dargen.evoplus.util.asObject
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

class Widget(id: String, name: String, supplier: Node.() -> Node) : Setting<Node>(id, name) {

    override var value: Node = OverlayContext + box {
        color = Color.WHITE.alpha(0)
        +supplier()
        drag(inOutHandler = {
            if (FeaturesScreen.isInWidgetEditor() && !it) {
                position += translation
                translation = v3()
            }
        }) { _, delta ->
            if (FeaturesScreen.isInWidgetEditor()){
                translation = delta / OverlayContext.Scale

                val minX = position.x + translation.x
                val minY = position.y + translation.y
                val maxX = minX + size.x * scale.x
                val maxY = minY + size.y * scale.y

                val (parentX, parentY) = parent!!.size

                if (minX < 0) {
                    translation.x -= minX
                }
                if (minY < 0) {
                    translation.y -= minY
                }
                if (maxX > parentX) {
                    translation.x -= maxX - parentX
                }
                if (maxY > parentY) {
                    translation.y -= maxY - parentY
                }
            }
        }
        preTransform { _, _ ->
            color = if (FeaturesScreen.isInWidgetEditor()) color.alpha(.3) else color.alpha(0)
        }
    }
    override val settingElement get() = DummyNode

    override fun load(element: JsonElement) {
        val element = element.asObject() ?: return

        element["position"].asObject()?.let {
            value.position.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
        element["scale"].asObject()?.let {
            value.scale.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
    }

    override fun store(): JsonElement =
        Gson.toJsonTree(mapOf("position" to value.translation.toMap(), "scale" to value.scale.toMap()))

}