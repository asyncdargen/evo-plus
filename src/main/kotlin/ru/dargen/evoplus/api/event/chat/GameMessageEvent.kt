package ru.dargen.evoplus.api.event.chat

import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
data class GameMessageEvent(val message: Text, val overlay: Boolean) : CancellableEvent() {

    val text get() = message.string

}