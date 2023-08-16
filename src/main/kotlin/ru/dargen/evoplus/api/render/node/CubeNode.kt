package ru.dargen.evoplus.api.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.drawCube
import ru.dargen.evoplus.util.render.translate

@KotlinOpens
class CubeNode : Node() {

    init {
        size = v3(1.0, 1.0, 1.0)
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!isSeeThrough) RenderSystem.enableDepthTest()
        matrices.drawCube(size, color.rgb)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun cube(block: CubeNode.() -> Unit = {}) = CubeNode().apply(block)