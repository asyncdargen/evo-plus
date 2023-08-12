package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.event.chat.ChatSendEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.OverlayContext
import ru.dargen.evoplus.api.render.node.minus
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.printMessage

val ModLabel = "§f§lEvo§6§lPlus"

val Logger = java.util.logging.Logger.getLogger("EvoPlus")

object EvoPlus : ModInitializer {

    override fun onInitialize() {
        EventBus
        KeyBindings

        OverlayContext
        AnimationRunner

        Features

        on<ChatSendEvent> {
            OverlayContext + rectangle {
                size = v3(10.0, 10.0)
                color = Colors.Black
                animate("test", 1.0) {
                    align = Relative.Center
                    origin = Relative.Center

                    after { printMessage("relatived") }
                }.next("scale", .4) {
                    scale = v3(2.0, 2.0,2.0)

                    after { printMessage("scaled") }
                }.next("stay", 2.0) {

                    after { printMessage("stayed") }
                }.next("lol", .8) {
                    translation = v3(99.0, 30.0)

                    after {
                        printMessage("moved removed")
                        OverlayContext - this@rectangle
                    }
                }
            }
        }
    }

}