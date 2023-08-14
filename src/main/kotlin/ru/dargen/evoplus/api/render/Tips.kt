package ru.dargen.evoplus.api.render

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawText
import ru.dargen.evoplus.util.render.fill
import ru.dargen.evoplus.util.render.translate
import java.awt.Color

object Tips {

    fun draw(
        matrices: MatrixStack, vararg lines: String, position: Vector3 = MousePosition,
        space: Double = 1.0, indent: Double = 2.5,
        color: Color = Colors.TransparentBlack,
        textColor: Color = Colors.White, shadow: Boolean = false
    ) {
        val width = lines.maxOf(TextRenderer::getWidth) + indent * 2
        val height = lines.size * TextRenderer.fontHeight + (lines.size - 1) * space + indent * 2

        matrices.push()
        matrices.translate(position)
//        matrices.scale(Overlay.Scale)

        matrices.fill(.0, .0, width, height, color.rgb)

        matrices.translate(indent, indent, .0)

        lines.forEach {
            matrices.drawText(it, color = textColor.rgb, shadow = shadow)

            matrices.translate(.0, TextRenderer.fontHeight + space, .0)
        }

        matrices.pop()
    }


}