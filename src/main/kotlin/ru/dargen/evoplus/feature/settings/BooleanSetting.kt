package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.util.asBoolean
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class BooleanSetting(id: String, name: String, value: Boolean) : Setting<Boolean>(id, name) {

    override var value: Boolean = value
        set(value) {
            field = value
            handler(value)
        }
    override val settingElement
        get() = button(value.stringfy()) {
            on {
                value = !value
                label.text = value.stringfy()
            }
        }

    override fun load(element: JsonElement) {
        value = element.asBoolean(value)
    }

    override fun store() = JsonPrimitive(value)

    fun Boolean.stringfy() = if (this) "§aВключено" else "§cВыключено"

}