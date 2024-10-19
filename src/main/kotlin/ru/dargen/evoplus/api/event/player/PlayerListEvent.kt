package ru.dargen.evoplus.api.event.player

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry
import ru.dargen.evoplus.api.event.Event

data class PlayerListEvent(val actions: Set<PlayerListS2CPacket.Action>, val entries: List<Entry>) : Event {

    val playersMap get() = actions.zip(entries).toMap()

}