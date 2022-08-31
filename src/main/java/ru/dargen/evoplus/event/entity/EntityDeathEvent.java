package ru.dargen.evoplus.event.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import ru.dargen.evoplus.event.Event;

@Getter
@AllArgsConstructor
public class EntityDeathEvent extends Event {

    protected final Entity entity;

}
