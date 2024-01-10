package ru.dargen.evoplus.feature

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import ru.dargen.evoplus.feature.screen.FeatureScreenElements
import ru.dargen.evoplus.feature.settings.SettingsGroup
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.minecraft.itemStack

abstract class Feature(id: String, name: String, val icon: ItemStack) {
    constructor(id: String, name: String, icon: Item) : this(id, name, itemStack(icon))

    val screen = FeatureScreenElements()
    val settings = SettingsGroup(id, name, screen)
    val widgets = settings.setting(WidgetGroup(screen))

    val screenSection get() = screen.elementsSection

    val id by settings::id
    val name by settings::name

    inline fun <reified T> config(name: String, value: T) = Features.config(name, value)

}