package ru.dargen.evoplus.api.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.render.drawRectangle

@KotlinOpens
class RectangleNode : Node() {

    var transparentRender = false

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!transparentRender && color.alpha == 0) return

        if (!isSeeThrough) RenderSystem.enableDepthTest()
        matrices.drawRectangle(size, color = color)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun rectangle(block: RectangleNode.() -> Unit = {}) = RectangleNode().apply(block)