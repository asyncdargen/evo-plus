package ru.dargen.evoplus.api.event.inventory

import net.minecraft.item.ItemStack
import ru.dargen.evoplus.api.event.CancellableEvent

class InventorySlotUpdateEvent(
    var syncId: Int,
    var slot: Int,
    var stack: ItemStack,
    var event: InventoryOpenEvent?,
    var isHidden: Boolean
) : CancellableEvent()