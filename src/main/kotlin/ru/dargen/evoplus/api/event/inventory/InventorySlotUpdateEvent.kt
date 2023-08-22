package ru.dargen.evoplus.api.event.inventory

import net.minecraft.item.ItemStack

class InventorySlotUpdateEvent(
    syncId: Int,
    var slot: Int,
    var stack: ItemStack,
    var openEvent: InventoryOpenEvent?,
    var isHidden: Boolean
) : InventoryEvent(syncId)