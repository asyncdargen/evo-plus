package ru.dargen.evoplus.event;

import lombok.val;
import ru.dargen.evoplus.EvoPlus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventBus {

    private final Map<Class<? extends Event>, Set<Consumer<Event>>> handlerLists = new ConcurrentHashMap<>();
    private final EvoPlus mod;

    public EventBus(EvoPlus mod) {
        this.mod = mod;
    }

    public <E extends Event> void register(Class<E> eventClass, Consumer<E> handler) {
        handlerLists.computeIfAbsent(eventClass, (__) -> new HashSet<>()).add((Consumer<Event>) handler);
    }

    public <E extends Event> Set<Consumer<Event>> getHandlerList(Class<E> eventClass) {
        return handlerLists.getOrDefault(eventClass, Collections.emptySet());
    }

    public <E extends Event> E fireEvent(E event) {
        val eventClass = event.getClass();
        val handlerList = getHandlerList(eventClass);

        handlerList.forEach(handler -> {
            try {
                handler.accept(event);
            } catch (Throwable t) {
                mod.getLogger().error("Error while handle event " + eventClass.getName(), t);
            }
        });

        return event;
    }

}
