package ru.dargen.evoplus.api.render.node

import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawText
import ru.dargen.evoplus.util.render.positionMatrix

@KotlinOpens
class TextNode(lines: List<String>) : Node() {
    constructor(vararg lines: String) : this(lines.toList())
    constructor(line: String) : this(line.split("\n"))

    val linesCount get() = lines.size
    var lines: List<String> = lines
        set(value) {
            field = value
            recompute()
        }
    var text: String
        get() = lines.joinToString("\n")
        set(value) {
            lines = value.split("\n")
        }
    var linesWithWidths: List<Pair<String, Int>> = emptyList()

    var linesSpace = 1.0
    var isShadowed = false
    var isCentered = false

    init {
        color = Colors.White
        recompute()
    }

    fun recompute() {
        linesWithWidths = lines.map { it to TextRenderer.getWidth(it) }
        size.set(
            (linesWithWidths.maxOfOrNull { it.second }?.toDouble() ?: .0),
            linesCount * (TextRenderer.fontHeight - 1.0) + (linesCount - 1) * linesSpace, .0
        )
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        val height = (TextRenderer.fontHeight - 1.0)
        linesWithWidths.forEachIndexed { index, (line, width) ->
            val x = if (isCentered) size.x / 2.0 - width / 2.0 else .0
            val y = index * height + index * linesSpace

            if (isWorldElement) {
                TextRenderer.draw(
                    text, x.toFloat(), y.toFloat(), color.rgb, isShadowed,
                    matrices.positionMatrix, World.VertexConsumers,
                    if (isSeeThrough) TextLayerType.SEE_THROUGH else TextLayerType.NORMAL,
                    0, LightmapTextureManager.MAX_LIGHT_COORDINATE
                )
                World.VertexConsumers.drawCurrentLayer()
            } else matrices.drawText(line, Vector3(x, y), isShadowed, color.rgb)
        }
    }

}

fun text(vararg lines: String, block: TextNode.() -> Unit = {}) = TextNode(*lines).apply(block)

fun text(lines: List<String>, block: TextNode.() -> Unit = {}) = TextNode(lines).apply(block)