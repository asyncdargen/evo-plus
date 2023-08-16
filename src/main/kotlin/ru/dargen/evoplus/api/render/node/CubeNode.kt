package ru.dargen.evoplus.api.render.node

import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.drawCube

@KotlinOpens
class CubeNode : Node() {

    init {
        size = v3(40.0, 40.0, 40.0)
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        matrices.drawCube(size, color.rgb)
    }

}

fun cube(block: CubeNode.() -> Unit = {}) = CubeNode().apply(block)