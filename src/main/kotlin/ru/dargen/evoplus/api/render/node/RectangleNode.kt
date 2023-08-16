package ru.dargen.evoplus.api.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.render.fill

@KotlinOpens
class RectangleNode : Node() {

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!isSeeThrough) RenderSystem.enableDepthTest()
        matrices.fill(v2 = size, color = color.rgb)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun rectangle(block: RectangleNode.() -> Unit = {}) = RectangleNode().apply(block)