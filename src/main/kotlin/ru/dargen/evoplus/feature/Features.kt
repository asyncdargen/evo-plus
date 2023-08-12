package ru.dargen.evoplus.feature

import com.google.gson.JsonObject
import ru.dargen.evoplus.api.event.game.MinecraftLoadedEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.Keybinds.MenuKey
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.feature.type.MiscFeature
import ru.dargen.evoplus.feature.type.RenderFeature
import ru.dargen.evoplus.feature.type.boss.BossTimerFeature
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.log
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText

data object Features {

    val SettingsFile = Paths.get("config/evo-plus.json")
    val List = mutableListOf<Feature>()
    
    init {
        on<MinecraftLoadedEvent> {
            load()
            loadSettings()
        }
        Runtime.getRuntime().addShutdownHook(thread(false, block = this::saveSettings))

        MenuKey.on { FeaturesScreen.open() }
    }

    fun load() {
        MiscFeature
        BossTimerFeature
        RenderFeature
    }

    fun loadSettings() {
        if (SettingsFile.parent?.exists() != true) {
            SettingsFile.parent?.createDirectories()
        }

        if (SettingsFile.exists()) runCatching {
            val json = Gson.fromJson(SettingsFile.reader(), JsonObject::class.java)

            List.associateBy { json.asJsonObject[it.settings.id] }
                .filterKeys { !it.isJsonNull }
                .forEach { (element, feature) -> feature.settings.load(element) }
        }.exceptionOrNull()?.log("Error while loading features settings")
    }

    fun saveSettings() {
        if (SettingsFile.parent?.exists() != true) {
            SettingsFile.parent?.createDirectories()
        }

        runCatching {
            val json = JsonObject()

            List.forEach { json.add(it.settings.id, it.settings.store()) }

            SettingsFile.writeText(Gson.toJson(json))
        }.exceptionOrNull()?.log("Error while saving features settings")
    }

}