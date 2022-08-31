package ru.dargen.evoplus.event.interact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.dargen.evoplus.event.CancellableEvent;

@Getter
@AllArgsConstructor
public class InteractItemEvent extends CancellableEvent {

    protected World world;
    protected ItemStack itemStack;

}
