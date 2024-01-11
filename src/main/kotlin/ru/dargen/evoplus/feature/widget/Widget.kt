package ru.dargen.evoplus.feature.widget

import com.google.gson.JsonElement
import ru.dargen.evoplus.api.render.Colors.Transparent
import ru.dargen.evoplus.api.render.Colors.TransparentWhite
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.Tips
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.asDouble
import ru.dargen.evoplus.util.asObject
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class Widget(id: String, name: String, supplier: Node.() -> Unit) : Setting<Node>(id, name) {

    var position = false
    var enabled
        get() = value.enabled
        set(enabled) {
            value.enabled = enabled
        }

    override var value: Node = Overlay + box {
        supplier()

        color = Transparent
        vWheel { _, wheel ->
            if (isWidgetEditor && isHovered) {
                scale = (scale + wheel / 10.0).fixIn(.2, 4.0)
                fix()
            }
        }
        click(2) { _, state ->
            if (isWidgetEditor && isHovered && state) animate("scale", .2) { scale = v3(1.0, 1.0, 1.0) }
        }
        drag(inOutHandler = {
            if (it && isWidgetEditor) usePosition()
            else if (!it && this@Widget.position) useAlign()
        }) { _, delta ->
            if (isWidgetEditor) {
                translation = delta / (wholeScale / scale)

                fixPosition()
            }
        }
        postRender { matrices, _ ->
            if (isWidgetEditor && isHovered) Tips.draw(
                matrices, "Для изменения размера используйте колесико мышки.",
                "Чтобы вернуть размер по умолчанию, нажмите на колесико мышки."
            )
        }
        preTransform { _, _ -> color = if (isWidgetEditor && isHovered) TransparentWhite else Transparent }
    }

    private fun fix() {
        usePosition()
        fixPosition()
        useAlign()
    }

    private fun fixPosition() = value.apply {

        val scale = (wholeScale / scale)
        val minPosition = wholePosition / scale
        val maxPosition = minPosition + wholeSize / scale

        val (minX, minY) = minPosition
        val (maxX, maxY) = maxPosition

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

    private fun usePosition() = value.apply {
        this@Widget.position = true

        position = parent!!.size * align
        align = v3()
    }

    private fun useAlign() = value.apply {
        this@Widget.position = false

        var pos = position + translation

        position = v3()
        translation = v3()

        val centeredAlign = ((pos - size * origin * scale + size * scale / 2.0) / parent!!.size).fixNaN()

        val origin = Relative.entries
            .minBy { it.distance(centeredAlign) }
        pos += (origin - this.origin) * size * scale

        this.origin = origin
        align = (pos / parent!!.size).fixNaN()
    }

    override fun load(element: JsonElement) {
        val element = element.asObject() ?: return

        element["origin"].asObject()?.let {
            value.origin.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
        element["align"].asObject()?.let {
            value.align.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
        element["scale"].asObject()?.let {
            value.scale.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
    }

    override fun store(): JsonElement = Gson.toJsonTree(
        mapOf(
            "align" to value.align.toMap(),
            "scale" to value.scale.toMap(),
            "origin" to value.origin.toMap()
        )
    )

}