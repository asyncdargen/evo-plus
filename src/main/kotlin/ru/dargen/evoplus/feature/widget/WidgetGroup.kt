package ru.dargen.evoplus.feature.widget

import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.feature.screen.FeatureScreenElements
import ru.dargen.evoplus.feature.settings.BooleanSetting
import ru.dargen.evoplus.feature.settings.SettingsGroup

class WidgetGroup(screen: FeatureScreenElements) : SettingsGroup("widgets", "Виджеты", screen) {

    fun widget(
        name: String, id: String = "",
        toggler: Boolean = true, widget: Node.() -> Unit
    ) = widget(Widget(id, name, widget), toggler)

    fun widget(widget: Widget, toggler: Boolean = true): Widget {
        setting(
            BooleanSetting("toggler-${widget.id}", "§6Виджет: §f${widget.name}", widget.enabled)
        ) on { widget.enabled = it }
        return setting(widget)
    }

}