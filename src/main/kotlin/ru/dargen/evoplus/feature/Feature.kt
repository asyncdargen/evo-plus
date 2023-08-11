package ru.dargen.evoplus.feature

import net.minecraft.item.Item
import ru.dargen.evoplus.feature.settings.SettingsGroup
import ru.dargen.evoplus.feature.widget.WidgetGroup

abstract class Feature(id: String, name: String, val icon: Item) {

    val settings = SettingsGroup(id, name)
    val widgets = settings.setting(WidgetGroup())

    val settingsSection get() = settings.settingSection

    val id by settings::name
    val name by settings::name

    init {
        Features.List.add(this)
    }

}