package ru.dargen.evoplus.api.event.inventory

import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.CancellableEvent

class InventoryOpenEvent(
    var syncId: Int,
    var screenHandlerType: ScreenHandlerType<*>,
    var name: Text,
    var isHidden: Boolean = false
) : CancellableEvent() {

    val nameString: String
        get() = name.string

    fun setName(name: String) {
        this.name = Text.of(name)
    }

}
