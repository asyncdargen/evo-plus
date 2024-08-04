package ru.dargen.evoplus.util

import net.minecraft.util.Identifier
import ru.dargen.crowbar.Accessors
import ru.dargen.evoplus.util.render.identifier
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.net.URI

enum class Social(val identifier: Identifier, val website: String) {
    
    DISCORD(identifier("textures/icons/gui/discord.png"), "https://discord.gg/tkuVE3fdKt"),
    MODRINTH(identifier("textures/icons/gui/modrinth.png"), "https://modrinth.com/mod/evoplus"),
    GITHUB(identifier("textures/icons/gui/github.png"), "https://github.com/asyncdargen/evo-plus"),
    ;
    
    fun open() {
        Accessors.unsafe().openField<Boolean>(GraphicsEnvironment::class.java, "headless").staticValue = false
        
        Desktop.getDesktop().browse(URI(website))
    }
}