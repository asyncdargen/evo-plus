package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.schduler.Scheduler
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.protocol.EvoPlusProtocol

val ModLabel = "§f§lEvo§6§lPlus"

val Logger = java.util.logging.Logger.getLogger("EvoPlus")

object EvoPlus : ModInitializer {

    override fun onInitialize() {
        EventBus
        Scheduler
        KeyBindings

        EvoPlusProtocol

        World
        Overlay
        AnimationRunner

        Features

//        listen<StatisticInfo> {
//            printMessage(it.data.entries.joinToString("\n") { (key, value) -> "$key: $value"})
//        }
//        listen<ClanInfo> {
//            printMessage(it.data.entries.joinToString("\n") { (key, value) -> "$key: $value"})
//        }
    }

}