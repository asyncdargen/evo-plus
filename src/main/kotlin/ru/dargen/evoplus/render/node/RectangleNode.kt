package ru.dargen.evoplus.render.node

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.render.fill

@KotlinOpens
class RectangleNode : Node() {

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        matrices.fill(v2 = size, color = color.rgb)
    }

}

fun rectangle(block: RectangleNode.() -> Unit = {}) = RectangleNode().apply(block)