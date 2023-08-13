package ru.dargen.evoplus.feature

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.api.event.game.MinecraftLoadedEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.keybind.Keybinds.MenuKey
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.feature.config.JsonConfig
import ru.dargen.evoplus.feature.misc.MiscFeature
import ru.dargen.evoplus.feature.type.RenderFeature
import ru.dargen.evoplus.feature.type.boss.BossTimerFeature
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.isNull
import ru.dargen.evoplus.util.log
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText

data object Features {

    val Folder = Paths.get("evo-plus").createDirectories()
    val SettingsFile = Folder.resolve("features.json")

    val Configs = mutableListOf<JsonConfig<*>>()

    val List = mutableListOf<Feature>()

    init {
        on<MinecraftLoadedEvent> {
            load()
            loadSettings()
        }
        Runtime.getRuntime().addShutdownHook(thread(false) {
            saveSettings()
            Configs.forEach { it.save() }
        })
        every(1, TimeUnit.MINUTES) { Configs.forEach { it.save() } }

        MenuKey.on { FeaturesScreen.open() }
    }

    fun load() {
        MiscFeature
        BossTimerFeature
        RenderFeature
    }

    inline fun <reified T> config(name: String, value: T) = JsonConfig(name, object : TypeToken<T>() {}, value).apply {
        load()
        Configs.add(this)
    }

    fun loadSettings() {
        if (SettingsFile.exists()) runCatching {
            val json = Gson.fromJson(SettingsFile.reader(), JsonObject::class.java)

            List.associateBy { json.asJsonObject[it.settings.id] }
                .filterKeys { !it.isNull }
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