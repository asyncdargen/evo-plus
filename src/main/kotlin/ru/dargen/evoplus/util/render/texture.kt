package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import org.lwjgl.system.MemoryStack
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.util.minecraft.Client
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


fun identifier(name: String) = Identifier(EvoPlus.ModContainer.metadata.id, name)

fun Identifier.bindTexture() {
    if (namespace == "evo-plus") {
        val file = ImageIO.read(EvoPlus::class.java.getResourceAsStream("/assets/evo-plus/$path"))
        file.uploadTexture(this)
    }
    
    RenderSystem.setShaderTexture(0, this)
}

fun BufferedImage.uploadTexture(id: String): Identifier {
    val identifier = identifier(id)
    
    uploadTexture(identifier)
    
    return identifier
}

fun BufferedImage.uploadTexture(identifier: Identifier) {
    val bytes = toByteArray()
    
    val byteBuffer = MemoryStack.create(bytes.size).malloc(bytes.size)
    byteBuffer.put(bytes)
    byteBuffer.rewind()
    val image = NativeImage.read(byteBuffer)
    
    val texture = NativeImageBackedTexture(image)
    
    Client.textureManager.registerTexture(identifier, texture)
}

fun BufferedImage.toByteArray() =
    ByteArrayOutputStream().apply { ImageIO.write(this@toByteArray, "png", this) }.toByteArray()