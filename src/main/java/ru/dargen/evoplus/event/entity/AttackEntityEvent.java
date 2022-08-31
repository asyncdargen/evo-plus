package ru.dargen.evoplus.event.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import ru.dargen.evoplus.event.CancellableEvent;

@Getter
@AllArgsConstructor
public class AttackEntityEvent extends CancellableEvent {

    protected Entity entity;

}
