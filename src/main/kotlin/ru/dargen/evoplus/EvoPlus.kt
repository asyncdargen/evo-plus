package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.OverlayContext
import ru.dargen.evoplus.feature.Features
import java.util.concurrent.Executors

val ModLabel = "§f§lEvo§6§lPlus"

val Logger = java.util.logging.Logger.getLogger("EvoPlus")
val Executor = Executors.newScheduledThreadPool(2)

object EvoPlus : ModInitializer {

    override fun onInitialize() {
        EventBus
        KeyBindings

        OverlayContext
        AnimationRunner

        Features
    }

}