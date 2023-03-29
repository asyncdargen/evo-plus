package ru.dargen.evoplus.event.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.dargen.evoplus.event.Event;

@Getter
@AllArgsConstructor
public class KeyboardEvent extends Event {

    protected int key, modifiers;
}
