package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import ru.dargen.evoplus.event.EventBus
import ru.dargen.evoplus.render.animation.AnimationHolder
import ru.dargen.evoplus.render.context.OverlayContext
import java.util.concurrent.Executors

val Logger = java.util.logging.Logger.getLogger("EvoPlus")
val Executor = Executors.newScheduledThreadPool(2)

object EvoPlus : ModInitializer {

    override fun onInitialize() {
        EventBus

        OverlayContext
        AnimationHolder
    }

}