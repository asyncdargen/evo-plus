package ru.dargen.evoplus.feature.config

import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.log
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class JsonConfig<T>(val name: String, val token: TypeToken<T>, var value: T) : ReadWriteProperty<Any, T> {

    val file = Features.Folder.resolve("$name.json")

    fun load() {
        runCatching {
            if (file.exists()) {
                value = Gson.fromJson(file.reader(), token)
            }
        }.exceptionOrNull()?.log("Error while loading config: $name.json")
    }

    fun save() {
        runCatching { file.writeText(Gson.toJson(value)) }
            .exceptionOrNull()
            ?.log("Error while saving config: $name.json")
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

}