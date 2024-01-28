package ru.dargen.evoplus.feature.config

import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.util.json.Gson
import ru.dargen.evoplus.util.catch
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class JsonConfig<T>(val name: String, val token: TypeToken<T>, var value: T) : ReadWriteProperty<Any, T> {

    val file = Features.Folder.resolve("$name.json")

    fun load() {
        catch("Error while loading config: $name.json") {
            if (file.exists()) value = Gson.fromJson(file.reader(), token)
        }
    }

    fun save() {
        catch("Error while saving config: $name.json") { file.writeText(Gson.toJson(value)) }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

}