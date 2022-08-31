package ru.dargen.evoplus.event;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class CancellableEvent extends Event {

    protected boolean cancelled;

}
