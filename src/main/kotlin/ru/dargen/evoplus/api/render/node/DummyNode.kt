package ru.dargen.evoplus.api.render.node

import net.minecraft.client.util.math.MatrixStack

data object DummyNode : Node() {

    override var enabled: Boolean
        get() = false
        set(value) {}

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {

    }

}