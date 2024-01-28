package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import ru.dargen.evoplus.api.render.node.input.selector.scroll.hScrollSelector
import ru.dargen.evoplus.feature.screen.FeatureBaseElement
import ru.dargen.evoplus.util.json.asInt
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector
import ru.dargen.evoplus.util.selector.observable

@KotlinOpens
class SelectorSetting<T>(
    id: String, name: String, selector: Selector<T>,
    var nameMapper: Selector<T>.(T?) -> String = { it.toString() }
) : Setting<T>(id, name) {

    val selector: Selector<T> = selector.observable().observe { handler(selected) }

    override var value: T
        get() = selector.selected
        set(value) {
            selector.select(value)
        }
    override val settingElement = FeatureBaseElement(name) {
        hScrollSelector<T> {
            this@hScrollSelector.selector = this@SelectorSetting.selector
            nameMapper = this@SelectorSetting.nameMapper
        }
    }

    override fun load(element: JsonElement) {
        selector.selectOn(element.asInt(selector.index))
    }

    override fun store() = JsonPrimitive(selector.index)

}