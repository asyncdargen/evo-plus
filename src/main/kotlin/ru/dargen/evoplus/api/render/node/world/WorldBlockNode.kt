package ru.dargen.evoplus.api.render.node.world

import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import org.joml.Matrix4f
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.util.render.drawWorldBlock

class WorldBlockNode(var x: Int, var y: Int, var z: Int) : Node() {

    override fun renderWorldElement(
        matrices: MatrixStack,
        tickDelta: Float,
        camera: Camera,
        positionMatrix: Matrix4f,
        vertexConsumers: VertexConsumerProvider.Immediate
    ) {
        matrices.drawWorldBlock(
            BlockPos(x, y, z),
            color.rgb
        )
    }
}

fun worldBlock(x: Int, y: Int, z: Int, block: WorldBlockNode.() -> Unit = {}) = WorldBlockNode(x, y, z).apply(block)