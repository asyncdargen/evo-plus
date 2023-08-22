package ru.dargen.evoplus.api.event

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.util.kotlin.cast
import java.util.logging.Level

typealias EventHandler<E> = E.() -> Unit

object EventBus {

    private val Handlers: MutableMap<Class<*>, MutableSet<EventHandler<*>>> = Object2ObjectOpenHashMap()

    init {
        ClientReceiveMessageEvents.ALLOW_CHAT.register { text, _, _, _, _ -> fireResult(ChatReceiveEvent(text)) }
        ClientReceiveMessageEvents.ALLOW_GAME.register { text, _ -> fireResult(ChatReceiveEvent(text)) }
    }

    fun <E : Event> register(type: Class<E>, handler: EventHandler<E>) =
        Handlers.getOrPut(type, ::ObjectOpenHashSet).add(handler.cast())

    fun <E : Event> fire(event: E) = event.apply {
        Handlers[event.javaClass]?.forEach { handler ->
            runCatching { handler.cast<EventHandler<E>>()(event) }
                .exceptionOrNull()
                ?.let { Logger.log(Level.SEVERE, "Error while event dispatch ${event.javaClass}", it) }
        }
    }

    fun <E : CancellableEvent> fireResult(event: E) = !fire(event).isCancelled

}

inline fun <reified E : Event> on(noinline handler: EventHandler<E>) = EventBus.register(E::class.java, handler)