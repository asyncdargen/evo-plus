package ru.dargen.evoplus.feature.type.chat

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.scroll.hScrollView
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class ChatSectionsSetting(
    id: String, name: String, override var value: MutableMap<ChatType, Boolean>
) : Setting<MutableMap<ChatType, Boolean>>(id, name) {

    override val settingSection: Node
        get() = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 55.0)
            +text(name) {
                translation = v3(x = 5.0, y = 12.0)
                align = Relative.LeftTop
                origin = Relative.LeftCenter
            }
            +settingElement.apply {
                translation = v3(x = 5.0, y = -17.5)
                align = Relative.LeftBottom
                origin = Relative.LeftCenter
            }
        }
    override val settingElement: Node
        get() = hScrollView {
            box.fixChildSize = false
            color = Colors.TransparentBlack
            size = v3(y = 25.0)
            preTransform { _, _ -> size.x = parent!!.size.x - 10.0 }
            ChatType.entries.forEach {
                addElements(hbox {
                    size.y = 15.0
                    dependSizeY = false
                    +text(it.displayName) { isShadowed = false }
                    preTransform { _, _ -> color = if (get(it)) Colors.Positive else Colors.Negative }
                    leftClick { _, state -> if (isHovered && state) toggle(it) }
                })
            }
        }

    operator fun get(type: ChatType) = value[type] ?: false

    fun toggle(type: ChatType) = (!get(type)).also { value[type] = it }

    override fun load(element: JsonElement) {
        if (element.isJsonObject) {
            Gson.fromJson(element.asJsonObject, object : TypeToken<MutableMap<ChatType, Boolean>>() {})
        }
    }

    override fun store(): JsonElement = Gson.toJsonTree(value)

}