package ru.dargen.evoplus.api.event.inventory

import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.util.minecraft.asText

class InventoryOpenEvent(
    var syncId: Int,
    var screenHandlerType: ScreenHandlerType<*>,
    var name: Text,
    var isHidden: Boolean = false
) : CancellableEvent() {

    val nameString: String
        get() = name.string

    fun setName(name: String) {
        this.name = name.asText
    }

}
