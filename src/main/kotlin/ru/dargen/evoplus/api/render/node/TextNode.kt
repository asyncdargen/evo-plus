package ru.dargen.evoplus.api.render.node

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawText

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
    var linesWithWidths: Map<String, Int> = emptyMap()

    var linesSpace = 1.0
    var isShadowed = false
    var isCentered = false

    init {
        color = Colors.White
        recompute()
    }

    fun recompute() {
        linesWithWidths = lines.associateWith(TextRenderer::getWidth)
        size.set(
            (linesWithWidths.values.maxOrNull()?.toDouble() ?: .0),
            linesCount * (TextRenderer.fontHeight - 1.0) + (linesCount - 1) * linesSpace, .0
        )
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        val height = (TextRenderer.fontHeight - 1.0)
        linesWithWidths.entries.forEachIndexed { index, (line, width) ->
            val x = if (isCentered) size.x / 2.0 - width / 2.0 else .0
            val y = index * height + index * linesSpace

            matrices.drawText(line, Vector3(x, y), isShadowed, color.rgb)
        }
    }

}

fun text(vararg lines: String, block: TextNode.() -> Unit = {}) = TextNode(*lines).apply(block)

fun text(lines: List<String>, block: TextNode.() -> Unit = {}) = TextNode(lines).apply(block)