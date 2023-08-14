package ru.dargen.evoplus.feature.widget

import com.google.gson.JsonElement
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Tips
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.asDouble
import ru.dargen.evoplus.util.asObject
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.MousePosition

class Widget(id: String, name: String, supplier: Node.() -> Node) : Setting<Node>(id, name) {

    override var value: Node = Overlay + box {
        +supplier()

        color = Colors.Transparent
        vWheel { _, wheel ->
            if (FeaturesScreen.isInWidgetEditor() && isHovered) scale = (scale + wheel / 10.0).fixIn(.2, 4.0)
        }
        click(2) { _, state ->
            if (FeaturesScreen.isInWidgetEditor() && isHovered && state) animate("scale", .2) {
                scale = v3(1.0, 1.0, 1.0)
            }
        }
        drag(inOutHandler = {
            if (FeaturesScreen.isInWidgetEditor() && !it) {
                position += translation
                translation = v3()
            }
        }) { _, delta ->
            if (FeaturesScreen.isInWidgetEditor()) {
                translation = delta / Overlay.Scale

                val minX = position.x + translation.x
                val minY = position.y + translation.y
                val maxX = minX + size.x * scale.x
                val maxY = minY + size.y * scale.y

                val (parentX, parentY) = parent!!.size

                if (minX < 0) {
                    translation.x -= minX
                } else if (maxX > parentX) {
                    translation.x -= maxX - parentX
                }

                if (minY < 0) {
                    translation.y -= minY
                } else if (maxY > parentY) {
                    translation.y -= maxY - parentY
                }
            }
        }
        postRender { matrices, _ ->
            if (FeaturesScreen.isInWidgetEditor() && isHovered) Tips.draw(
                matrices, "Для изменения размера используйте колесико мышки.",
                "Чтобы вернуть размер по умолчанию, нажмите на колесико мышки.",
                position = (MousePosition - wholePosition) / Overlay.Scale
            )
        }
        preTransform { _, _ ->
            color = if (FeaturesScreen.isInWidgetEditor()) Colors.TransparentWhite else Colors.Transparent
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
        Gson.toJsonTree(mapOf("position" to value.position.toMap(), "scale" to value.scale.toMap()))

}