package ru.dargen.evoplus.feature.widget

import com.google.gson.JsonElement
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.asDouble
import ru.dargen.evoplus.util.asObject
import ru.dargen.evoplus.util.math.v3

class Widget(id: String, name: String, supplier: Node.() -> Node) : Setting<Node>(id, name) {

    override var value: Node = Overlay +box {
        +supplier()

        val tooltip = Overlay + hbox {
            space = 1.0
            enabled = false
            color = Colors.TransparentBlack

            +text(
                "Для изменения размера используйте колесико мышки.",
                "Чтобы вернуть размер по умолчанию, нажмите на колесико мышки."
            )
        }

        color = Colors.Transparent
        vWheel { _, wheel ->
            if (FeaturesScreen.isInWidgetEditor() && isHovered) scale = (scale + wheel / 10.0).fixIn(.2, 4.0)
        }
        click(2) { _, state ->
            if (FeaturesScreen.isInWidgetEditor() && isHovered && state) animate("scale", .2) {
                scale = v3(1.0, 1.0, 1.0)
            }
        }
        hover { _, state -> tooltip.enabled = FeaturesScreen.isInWidgetEditor() && state }
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
            if (FeaturesScreen.isInWidgetEditor()) {
                if (isHovered) tooltip.position = Overlay.ScaledMouse
                color = Colors.TransparentWhite
            } else color = Colors.Transparent
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