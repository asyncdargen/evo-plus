package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.dargen.evoplus.feature.screen.FeatureScreenElements
import ru.dargen.evoplus.util.json.isNull
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector

@KotlinOpens
class SettingsGroup(id: String, name: String, val screen: FeatureScreenElements) :
    Setting<List<Setting<*>>>(id, name) {

    final val settings = mutableListOf<Setting<*>>()
    override var value: List<Setting<*>> = settings

    fun <S : Setting<*>> setting(setting: S) = setting.apply {
        settings.add(this)
        screen.element(settingElement)
    }

    fun boolean(name: String, value: Boolean = false, id: String = "") =
        setting(BooleanSetting(id, name, value))

    fun string(name: String, value: String = "", id: String = "") =
        setting(StringSetting(id, name, value))

    fun colorInput(name: String, value: String = "", id: String = "") =
        setting(ColorInputSetting(id, name))

    fun <T> selector(
        name: String, selector: Selector<T>,
        id: String = "",
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" }
    ) = setting(SelectorSetting(id, name, selector, nameMapper))

    fun <T> switcher(
        name: String, selector: Selector<T>,
        id: String = "",
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" }
    ) = setting(SwitcherSetting(id, name, selector, nameMapper))

    override fun load(element: JsonElement) {
        if (!element.isJsonObject) return

        settings.associateWith { element.asJsonObject[it.id] }
            .filterValues { !it.isNull }
            .forEach { (setting, settingElement) -> setting.load(settingElement) }
    }

    override fun store(): JsonElement {
        val group = JsonObject()

        settings.forEach { group.add(it.id, it.store()) }

        return group
    }

}