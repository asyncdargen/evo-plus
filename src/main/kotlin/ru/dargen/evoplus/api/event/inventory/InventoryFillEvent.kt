package ru.dargen.evoplus.api.event.inventory

import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import ru.dargen.evoplus.api.event.CancellableEvent

class InventoryFillEvent(
    var syncId: Int,
    var contents: List<ItemStack>,
    var openEvent: InventoryOpenEvent?,
    var screenHandler: ScreenHandler
) : CancellableEvent() {

    var isHidden = false

}

