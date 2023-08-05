package ru.dargen.evoplus.event.chat

import ru.dargen.evoplus.event.CancellableEvent

data class ChatSendEvent(val text: String) : CancellableEvent()