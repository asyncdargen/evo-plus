package ru.dargen.evoplus.api.event.network

import io.netty.buffer.ByteBuf
import ru.dargen.evoplus.api.event.CancellableEvent

class CustomPayloadEvent(val channel: String, val payload: ByteBuf) : CancellableEvent()