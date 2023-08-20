package ru.dargen.evoplus.util.minecraft

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.screen.slot.SlotActionType


object Inventories {

    fun close(syncId: Int) = sendPacket(CloseHandledScreenC2SPacket(syncId))

    fun click(syncId: Int = CurrentScreenHandler!!.syncId, slot: Int, revision: Int = 0) = sendPacket(
        ClickSlotC2SPacket(
            syncId, 0, slot, revision,
            SlotActionType.PICKUP, ItemStack.EMPTY,
            Int2ObjectOpenHashMap()
        )
    )

}