package ru.dargen.evoplus.api.event.inventory

import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import ru.dargen.evoplus.util.minecraft.asText

class InventoryOpenEvent(
    syncId: Int,
    var screenHandlerType: ScreenHandlerType<*>,
    var title: Text,
    var isHidden: Boolean = false
) : InventoryEvent(syncId) {

    var nameString: String
        get() = title.string
        set(value) {
            title = value.asText()
        }

}
