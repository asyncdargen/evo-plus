package ru.dargen.evoplus.api.render.node.world

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.drawCubeOutline

@KotlinOpens
class CubeOutlineNode : Node() {

    init {
        size = v3(1.0, 1.0, 1.0)
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!isSeeThrough) RenderSystem.enableDepthTest()
        matrices.drawCubeOutline(size, color)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun cubeOutline(block: CubeOutlineNode.() -> Unit = {}) = CubeOutlineNode().apply(block)