package ru.dargen.evoplus.api.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawRectangle
import ru.dargen.evoplus.util.render.drawText
import ru.dargen.evoplus.util.render.translate
import java.awt.Color

object Tips {

    fun draw(
        matrices: MatrixStack, vararg lines: String, position: Vector3 = MousePosition.apply { x += 5 },
        space: Float = 1.0f, indent: Float = 2.5f,
        color: Color = Colors.TransparentBlack,
        textColor: Color = Colors.White, shadow: Boolean = false
    ) {
        val width = lines.maxOf(TextRenderer::getWidth).toFloat() + indent * 2f
        val height = lines.size * TextRenderer.fontHeight + (lines.size - 1) * space + indent * 2

        if (width + position.x > Overlay.WindowSize.x) {
            position.x = (Overlay.WindowSize.x - width) - 1
        }
        if (height + position.y > Overlay.WindowSize.y) {
            position.y = (Overlay.WindowSize.y - height) - 1
        }


        matrices.push()
        matrices.loadIdentity()
        RenderSystem.disableScissor()

        matrices.translate(position)
        matrices.translate(0f, 0f, 1000f) //z buffer hehe
        matrices.drawRectangle(0f, 0f, width, height, color = color)

        matrices.translate(indent, indent, 0f)

        lines.forEach {
            matrices.drawText(it, shadow = shadow, color = textColor)

            matrices.translate(0f, TextRenderer.fontHeight + space, 0f)
        }

        matrices.pop()
    }


}