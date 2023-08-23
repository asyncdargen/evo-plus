package ru.dargen.evoplus.api.event.game

import io.netty.buffer.ByteBuf
import ru.dargen.evoplus.api.event.CancellableEvent

class CustomPayloadEvent(val channel: String, val payload: ByteBuf) : CancellableEvent()