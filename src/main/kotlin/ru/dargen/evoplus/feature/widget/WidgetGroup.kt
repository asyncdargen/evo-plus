package ru.dargen.evoplus.feature.widget

import ru.dargen.evoplus.api.render.node.DummyNode
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.feature.settings.SettingsGroup

class WidgetGroup : SettingsGroup("widgets", "Виджеты") {

    override val settingSection get() = DummyNode

    fun add(id: String, name: String, supplier: Node.() -> Node) = setting(Widget(id, name, supplier))

}