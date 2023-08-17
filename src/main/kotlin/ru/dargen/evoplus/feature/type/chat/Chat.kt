package ru.dargen.evoplus.feature.type.chat

import net.minecraft.text.Text

const val MAX_CHAT_SIZE = 100

data class Chat(val type: ChatType, val messages: MutableList<Text>) {

    var unreadCount = 0
        set(value) {
            field = value.coerceIn(0, MAX_CHAT_SIZE)
        }

    fun read() {
        unreadCount = 0
    }

    fun append(message: Text) {
        unreadCount++
        messages.add(message)

        while (messages.size > MAX_CHAT_SIZE) {
            unreadCount--
            messages.removeFirst()
        }
    }

}