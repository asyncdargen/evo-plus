package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.diamondworld.Connector
import ru.dargen.evoplus.feature.Features

val ModLabel = "§f§lEvo§6§lPlus"

val Logger = java.util.logging.Logger.getLogger("EvoPlus")

object EvoPlus : ModInitializer {

    override fun onInitialize() {
        EventBus
        KeyBindings

        World
        Overlay
        AnimationRunner

        Connector

        Features
    }

}