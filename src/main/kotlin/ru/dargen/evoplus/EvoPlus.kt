package ru.dargen.evoplus

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.WorldContext
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.scheduler.Scheduler
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.service.EvoPlusService
import ru.dargen.evoplus.util.Updater
import ru.dargen.evoplus.util.minecraft.Client
import java.util.concurrent.TimeUnit


val ModLabel = "§f§lEvo§6§lPlus"

val Logger = LoggerFactory.getLogger(EvoPlus::class.java)

object EvoPlus : ClientModInitializer {

    val ModContainer by lazy { FabricLoader.getInstance().getModContainer("evo-plus").get() }

    override fun onInitializeClient() {
        EventBus
        Scheduler
        KeyBindings
        ReplacerParser

        EvoPlusProtocol

        WorldContext
        Overlay
        AnimationRunner

        Features
        EvoPlusService

        scheduleUpdater()
    }

    private fun scheduleUpdater() = scheduleEvery(5, 5, unit = TimeUnit.MINUTES) {
        if (Client?.inGameHud != null && Updater.Outdated) Notifies.showText(
            "Обнаружена новая версия EvoPlus - §e${Updater.LatestVersion}",
            "Нажмите, чтобы обновиться.",
            delay = 15.0
        ) {
            leftClick { _, state ->
                if (isHovered && state) {
                    Updater.tryUpdate()
                    true
                } else false
            }
        }
    }

}