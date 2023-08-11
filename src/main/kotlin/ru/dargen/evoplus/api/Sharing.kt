package ru.dargen.evoplus.api

import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent

typealias ShareHandler = (player: String, key: String) -> Unit

object Sharing {

    val Handlers = mutableMapOf<String, ShareHandler>()

    init {
        ru.dargen.evoplus.api.event.on<ChatReceiveEvent> {

        }
    }

    fun on(channel: String, handler: ShareHandler) = Handlers.put(channel, handler)

}