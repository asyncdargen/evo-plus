package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.feature.screen.FeatureScreenElement
import ru.dargen.evoplus.feature.screen.FeatureBaseElement
import ru.dargen.evoplus.util.json.asBoolean
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class BooleanSetting(id: String, name: String, value: Boolean) : Setting<Boolean>(id, name) {

    override var value: Boolean = value
        set(value) {
            field = value
            handler(value)
        }

    override val settingElement: FeatureScreenElement = FeatureBaseElement(name) {
        button(this@BooleanSetting.value.stringfy()) {
            on {
                this@BooleanSetting.value = !this@BooleanSetting.value
                label.text = this@BooleanSetting.value.stringfy()
            }
        }
    }

    override fun load(element: JsonElement) {
        value = element.asBoolean(value)
    }

    override fun store() = JsonPrimitive(value)

    fun Boolean.stringfy() = if (this) "§aВключено" else "§cВыключено"

}