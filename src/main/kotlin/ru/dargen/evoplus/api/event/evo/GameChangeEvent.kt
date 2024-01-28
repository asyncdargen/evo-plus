package ru.dargen.evoplus.api.event.evo

import pro.diamondworld.protocol.packet.game.GameEvent
import ru.dargen.evoplus.api.event.Event

class GameChangeEvent(val old: GameEvent.EventType, val new: GameEvent.EventType) : Event