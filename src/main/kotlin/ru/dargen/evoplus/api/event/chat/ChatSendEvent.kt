package ru.dargen.evoplus.api.event.chat

import ru.dargen.evoplus.api.event.CancellableEvent

data class ChatSendEvent(var text: String) : CancellableEvent()