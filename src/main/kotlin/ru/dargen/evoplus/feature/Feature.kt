package ru.dargen.evoplus.feature

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import ru.dargen.evoplus.feature.settings.SettingsGroup
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.minecraft.itemStack

abstract class Feature(id: String, name: String, val icon: ItemStack, vararg val description: String) {
    constructor(id: String, name: String, icon: Item, vararg description: String) :
            this(id, name, itemStack(icon), *description)

    val settings = SettingsGroup(id, name)
    val widgets = settings.setting(WidgetGroup())

    val settingsSection get() = settings.settingSection

    val id by settings::name
    val name by settings::name

    init {
        Features.List.add(this)
    }

    inline fun <reified T> config(name: String, value: T) = Features.config(name, value)

}