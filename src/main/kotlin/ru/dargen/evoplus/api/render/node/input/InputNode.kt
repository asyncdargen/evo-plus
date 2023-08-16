package ru.dargen.evoplus.api.render.node.input

import net.minecraft.SharedConstants
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.util.format.safeSlice
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.fill
import java.awt.Color

typealias InputFilter = InputNode.(char: Char) -> Boolean

@KotlinOpens
class InputNode : RectangleNode() {

    val prompt = +text {
        position = v3(5.0)
        align = Relative.LeftCenter
        origin = Relative.LeftCenter
    }
    val text = +text {
        position = v3(5.0)
        align = Relative.LeftCenter
        origin = Relative.LeftCenter
    }
    var textColor: Color = Colors.White
        set(value) {
            field = value
            text.color = field
            prompt.color = field.darker()
        }

    var focused = false

    var content = ""
    val contentBefore
        get() = if (content.isEmpty()) "" else content.substring(0, cursor)

    val length get() = content.length
    var maxLenth = Int.MAX_VALUE

    var cursor = 0
        get() {
            field = field.coerceIn(0, content.length)

            return field
        }
    var filters = mutableSetOf<InputFilter>()
    var inputHandler: InputNode.(content: String) -> Unit = {}

    init {
        isScissor = true
        scissorIndent = v3(4.0, 4.0)
        size = v3(100.0, 20.0)
        textColor = Colors.White
        color = Colors.Second

        click { _, _, _ ->
            if (focused != isHovered) {
                cursor = if (isHovered) content.length else 0
            }
            focused = isHovered
        }
        type { char, _ -> if (focused) put(char.toString()) }

        typeKey(InputUtil.GLFW_KEY_RIGHT) { if (focused) cursor++ }
        typeKey(InputUtil.GLFW_KEY_LEFT) { if (focused) cursor-- }
        typeKey(InputUtil.GLFW_KEY_DELETE) { if (focused) remove(1) }
        typeKey(InputUtil.GLFW_KEY_BACKSPACE) { if (focused) remove(-1) }

        typeKey { if (focused && Screen.isPaste(it)) paste() }

        preTransform { _, _ ->
            text.text = content
            prompt.enabled = !focused && content.isEmpty()
            text.translation.x =
                -(TextRenderer.getWidth(contentBefore) * text.scale.x - (size.x - scissorIndent.x * 2 - 5.0))
                    .coerceAtLeast(.0)
        }
        text.postRender { matrices, tickDelta ->
            if (!focused || (System.currentTimeMillis() / 100) % 2 == 0L) return@postRender
            val preCursorSize = TextRenderer.getWidth(contentBefore) * scale.x
            matrices.fill(
                preCursorSize, -this@InputNode.size.y / 2,
                preCursorSize + 2.0, this@InputNode.size.y / 2,
                color.rgb
            )
        }
    }

    fun put(text: String) {
        val allowed = (maxLenth - length).coerceAtLeast(0)
        val text = text.filter { filters.all { filter -> filter(it) } }.safeSlice(0, allowed)

        content = content.substring(0, cursor) + text + content.substring(cursor)
        cursor += text.length

        if (text.isNotEmpty()) {
            inputHandler(content)
        }
    }

    fun remove(shift: Int) {
        val oldCursor = cursor
        cursor += shift
        content =
            if (shift > 0) {
                content.substring(0, oldCursor) + content.substring(cursor).apply { cursor -= shift }
            } else content.substring(0, cursor) + content.substring(oldCursor)
    }

    fun paste() {
        put(Client.keyboard.clipboard.replace("\n", "").replace("\\n", ""))
    }

    fun on(handler: InputNode.(content: String) -> Unit) = apply { inputHandler = handler }

    fun filter(filter: InputFilter) = apply { filters.add(filter) }

    fun strictSymbols() = filter { SharedConstants.isValidChar(it) }

}

fun input(block: InputNode.() -> Unit = {}) = InputNode().apply(block)