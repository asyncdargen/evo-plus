package ru.dargen.evoplus.feature.settings

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.input.button

class ActionSetting(name: String) : Setting<Any?>("", name) {

    override var value: Any? = null

    override val settingElement: Node
        get() = button(name) { on { handler(null) } }

    init {
        isStorable = false
    }

    override fun load(element: JsonElement) {

    }

    override fun store() = JsonNull.INSTANCE

}