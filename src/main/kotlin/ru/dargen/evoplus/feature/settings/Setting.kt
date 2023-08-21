package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.rectangle
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.v3
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias SettingHandler<T> = (T) -> Unit

@KotlinOpens
abstract class Setting<T>(var id: String, val name: String) : ReadWriteProperty<Any, T> {

    abstract var value: T
    var isStorable = true
    var description = emptyArray<String>()
    var handler: SettingHandler<T> = {}

    val settingSection: Node
        get() = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 30.0)
            +text(name) {
                translation = v3(x = 5.0)
                align = Relative.LeftCenter
                origin = Relative.LeftCenter
            }
            +settingElement.apply {
                translation = v3(x = -5.0)
                align = Relative.RightCenter
                origin = Relative.RightCenter
            }
        }

    abstract val settingElement: Node

    infix fun on(handler: SettingHandler<T>) = apply { this.handler = handler }

    fun description(vararg description: String) = apply { this.description = description.cast() }

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