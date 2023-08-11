package ru.dargen.evoplus.api.render.node

import net.minecraft.client.util.math.MatrixStack

class DelegateNode : Node() {

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        children.forEach { it.size = size.clone() }
    }

}

fun delegate(block: DelegateNode.() -> Unit = {}) = DelegateNode().apply(block)