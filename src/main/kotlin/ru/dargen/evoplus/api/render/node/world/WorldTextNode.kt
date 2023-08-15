package ru.dargen.evoplus.api.render.node.world

import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.asText
import ru.dargen.evoplus.util.render.VerticalAlign
import ru.dargen.evoplus.util.render.drawWorldText
import ru.dargen.evoplus.util.render.toVec3d

class WorldTextNode(var lines: List<String>) : Node() {

    constructor(vararg lines: String) : this(lines.toList())

    var verticalAlign = VerticalAlign.CENTER

    init {
        scale = v3(-0.025, -0.025, -1.0)
    }

    override fun renderWorldElement(
        matrices: MatrixStack,
        tickDelta: Float,
        camera: Camera,
        positionMatrix: Matrix4f,
        vertexConsumers: VertexConsumerProvider.Immediate
    ) {
        matrices.drawWorldText(
            position.toVec3d,
            camera,
            vertexConsumers,
            lines.map(String::asText),
            verticalAlign
        )
    }
}

fun worldText(vararg lines: String, block: WorldTextNode.() -> Unit = {}) = WorldTextNode(*lines).apply(block)

fun worldText(lines: List<String>, block: WorldTextNode.() -> Unit = {}) = WorldTextNode(lines).apply(block)