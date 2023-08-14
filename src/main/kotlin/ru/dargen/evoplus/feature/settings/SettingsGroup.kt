package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.node.DummyNode
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.isNull
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector

@KotlinOpens
class SettingsGroup(id: String, name: String) : Setting<MutableList<Setting<*>>>(id, name) {

    val settings = mutableListOf<Setting<*>>()
    override var value = settings
        set(value) {}

    override val settingSection: Node
        get() = vScrollView {
            box.color = Colors.TransparentBlack
            addElements(settings.map(Setting<*>::settingSection).filter { it !== DummyNode })
        }
    override val settingElement: Node
        get() = DummyNode

    fun <S : Setting<*>> setting(setting: S) = setting.apply(settings::add)

    fun boolean(id: String, name: String, value: Boolean = false) =
        setting(BooleanSetting(id, name, value))

    fun <T> selector(
        id: String, name: String, selector: Selector<T>,
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" }
    ) = setting(SelectorSetting(id, name, selector, nameMapper))

    fun <T> switcher(
        id: String, name: String, selector: Selector<T>,
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" }
    ) = setting(SwitcherSetting(id, name, selector, nameMapper))

    override fun load(element: JsonElement) {
        if (!element.isJsonObject) return

        value.associateWith { element.asJsonObject[it.id] }
            .filterValues { !it.isNull }
            .forEach { (setting, settingElement) -> setting.load(settingElement) }
    }

    override fun store(): JsonElement {
        val group = JsonObject()

        settings.forEach { group.add(it.id, it.store()) }

        return group
    }

}