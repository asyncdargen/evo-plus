package ru.dargen.evoplus.api.event.chat

import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
data class ChatReceiveEvent(var message: Text) : CancellableEvent() {

    val text get() = message.string

}