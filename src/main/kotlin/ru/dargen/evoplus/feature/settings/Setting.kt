package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import ru.dargen.evoplus.feature.screen.FeatureScreenElement
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias SettingHandler<T> = (T) -> Unit

@KotlinOpens
abstract class Setting<T>(var id: String, val name: String) : ReadWriteProperty<Any, T> {

    abstract var value: T
    var handler: SettingHandler<T> = {}

    val settingElement: FeatureScreenElement = FeatureScreenElement.Dummy

    infix fun on(handler: SettingHandler<T>) = apply { this.handler = handler }

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

    operator fun provideDelegate(thisRef: Any, property: KProperty<*>) = apply {
        id = id.ifBlank {
            property.name.foldIndexed("") { index, acc, char ->
                "$acc${(if (char.isUpperCase() && index > 0) "-" else "")}${char.lowercase()}"
            }
        }
    }

    abstract fun load(element: JsonElement)

    abstract fun store(): JsonElement

}