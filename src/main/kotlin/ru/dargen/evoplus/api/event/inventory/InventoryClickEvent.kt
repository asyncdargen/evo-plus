package ru.dargen.evoplus.api.event.inventory

import net.minecraft.screen.slot.SlotActionType

class InventoryClickEvent(
    syncId: Int, val slot: Int,
    val button: Int, val action: SlotActionType
) : InventoryEvent(syncId)