package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import ru.dargen.evoplus.util.minecraft.Client
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun identifier(name: String) = Identifier("evo-plus", name)

fun Identifier.bindTexture() {
    RenderSystem.setShaderTexture(0, this)
}

fun BufferedImage.uploadTexture(id: String): Identifier {
    val identifier = identifier(id)

    uploadTexture(identifier)

    return identifier
}

fun BufferedImage.uploadTexture(identifier: Identifier): AbstractTexture {
    val texture = NativeImageBackedTexture(NativeImage.read(toByteArray()))

    Client.textureManager.registerTexture(identifier, texture)

    return texture
}

fun BufferedImage.toByteArray() =
    ByteArrayOutputStream().apply { ImageIO.write(this@toByteArray, "png", this) }.toByteArray()