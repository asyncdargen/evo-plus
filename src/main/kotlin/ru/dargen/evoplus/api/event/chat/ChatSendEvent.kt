package ru.dargen.evoplus.api.event.chat

import ru.dargen.evoplus.api.event.CancellableEvent

data class ChatSendEvent(val text: String) : CancellableEvent()