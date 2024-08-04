package ru.dargen.evoplus.util

import net.fabricmc.loader.api.FabricLoader
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.common.LazyExpiringReference
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.channels.Channels
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.concurrent.thread
import kotlin.io.path.deleteIfExists

object Updater {

    private const val DOWNLOAD_URL = "https://github.com/asyncdargen/evo-plus/releases/download/%s/evo-plus.jar"
    private const val PROJECT_PROPERTIES_URL =
        "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/gradle.properties"

    val ModVersion by lazy { EvoPlus.ModContainer.metadata.version.friendlyString }
    val LatestVersion by LazyExpiringReference(2, TimeUnit.MINUTES) {
        Properties().apply { load(URL(PROJECT_PROPERTIES_URL).openStream()) }.getProperty("mod_version")
    }

    val IsDevEnvironment = java.lang.Boolean.getBoolean("evo-plus.dev")
    val Outdated get() = !IsDevEnvironment && ModVersion < LatestVersion

    val ModFiles = FabricLoader.getInstance().getModContainer("evo-plus").get().origin.paths

    fun tryUpdate() {
        runCatching {
            if (Outdated) update()
        }.exceptionOrNull()?.let { Logger.log(Level.SEVERE, "Error while attempting update", it) }
    }

    fun update() = screen {
        color = Colors.TransparentBlack

        +vbox {
            indent = v3()
            space = 20.0

            childrenRelative = .5

            origin = Relative.Center
            align = Relative.Center

            +text(
                "Обнаружена новая версия EvoPlus - §e$LatestVersion.",
                "",
                "Возможно потребуется обновить другие моды, подробнее",
                "вы можете ознакомиться с обновлениям на нашем дискорд сервере.",
                "",
                "Для выхода с обновлением нажмите кнопку \"Обновить\"."
            ) {
                scale = v3(1.1, 1.1)
                isCentered = true
            }

            +hbox {
                indent = v3()
                space = 3.0
                
                +button("Дискорд сервер") { on { Social.DISCORD.open() } }
                +button("Обновить") {
                    buttonColor = Colors.Green
                    on {
                        catch("Error while downloading latest mod version") {
                            val input = Channels.newChannel(URL(DOWNLOAD_URL.format(LatestVersion)).openStream())
                            val output = FileOutputStream(File("mods", "evo-plus-$LatestVersion.jar")).channel
                            output.transferFrom(input, 0, Long.MAX_VALUE)
                        }

                        thread(true, true) {
                            Thread.sleep(500)
                            ModFiles.forEach { it.deleteIfExists() }

                            Client.scheduleStop()
                        }

                        catch("Error while closing classloader") {
                            val loader = Updater::class.java.classLoader.parent as URLClassLoader
                            loader.close()
                        }
                    }
                }
            }
        }
    }.open()

}