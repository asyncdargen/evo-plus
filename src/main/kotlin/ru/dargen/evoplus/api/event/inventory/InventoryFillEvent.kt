package ru.dargen.evoplus.api.event.inventory

import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler

class InventoryFillEvent(
    syncId: Int,
    var contents: List<ItemStack>,
    var openEvent: InventoryOpenEvent?,
    var screenHandler: ScreenHandler,
    var isHidden: Boolean = false
) : InventoryEvent(syncId)
