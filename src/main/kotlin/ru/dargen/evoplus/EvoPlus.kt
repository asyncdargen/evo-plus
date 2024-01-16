package ru.dargen.evoplus

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.joml.Math
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.event.entity.EntitySpawnEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.plus
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.Scheduler
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.util.Updater
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.Player
import java.util.concurrent.TimeUnit


val ModLabel = "§f§lEvo§6§lPlus"

val Logger = java.util.logging.Logger.getLogger("EvoPlus")

object EvoPlus : ModInitializer {

    val ModContainer by lazy { FabricLoader.getInstance().getModContainer("evo-plus").get() }

    override fun onInitialize() {
        EventBus
        Scheduler
        KeyBindings

        EvoPlusProtocol

        World
        Overlay
        AnimationRunner

        Features

        scheduleUpdater()

        on<EntitySpawnEvent> {
            World + text(entity.type.translationKey) {
                origin = Relative.Center
                preTransform { matrices, tickDelta ->
                    val entityPos = entity.getLerpedPos(tickDelta)
                    position = entityPos.run { v3(x, y + entity.height + .3, z) }
                    rotation.y = Math.toRadians(Player!!.yaw.toDouble())
                    rotation.x = Math.toRadians(-Player!!.pitch.toDouble())
                }
            }
        }
//        listen<StatisticInfo> {
//            printMessage(it.data.entries.joinToString("\n") { (key, value) -> "$key: $value"})
//        }
//        listen<ClanInfo> {
//            printMessage(it.data.entries.joinToString("\n") { (key, value) -> "$key: $value"})
//        }
    }

    private fun scheduleUpdater() = scheduleEvery(5, 5, unit = TimeUnit.MINUTES) {
        if (Client?.inGameHud != null && Updater.Outdated) Notifies.showText(
            "Обнаружена новая версия EvoPlus - §e${Updater.LatestVersion}.",
            "Нажмите, чтобы обновиться.",
            delay = 15.0
        ) { leftClick { _, state -> if (isHovered && state) Updater.tryUpdate() } }
    }

}