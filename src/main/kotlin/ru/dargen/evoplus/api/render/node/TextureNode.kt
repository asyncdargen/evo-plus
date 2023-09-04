package ru.dargen.evoplus.api.render.node

import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import ru.dargen.evoplus.api.render.animation.property.proxied
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.bindTexture

@KotlinOpens
class TextureNode : Node() {

    lateinit var identifier: Identifier

    var textureOffset by proxied(v3())
    var textureSize by proxied(v3(256.0, 256.0))
    var repeating = false

    init {
        size = textureSize.clone()
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!this::identifier.isInitialized) return

        identifier.bindTexture()

        if (repeating) DrawableHelper.drawRepeatingTexture(
            matrices, 0, 0,
            size.x.toInt(), size.y.toInt(),
            textureOffset.x.toInt(), textureOffset.y.toInt(),
            textureSize.x.toInt(), textureSize.y.toInt()
        ) else DrawableHelper.drawTexture(
            matrices, 0, 0,
            textureOffset.x.toFloat(), textureOffset.y.toFloat(),
            size.x.toInt(), size.y.toInt(),
            textureSize.x.toInt(), textureSize.y.toInt()
        )
    }

}

fun texture(block: TextureNode.() -> Unit = {}) = TextureNode().apply(block)

fun texture(identifier: Identifier, block: TextureNode.() -> Unit = {}) = texture {
    this.identifier = identifier
    block()
}