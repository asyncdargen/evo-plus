package ru.dargen.evoplus.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import ru.dargen.evoplus.event.CancellableEvent;

@Getter @Setter
@AllArgsConstructor
public class InventorySlotUpdateEvent extends CancellableEvent {

    protected int syncId;
    protected int slot;
    protected ItemStack stack;
    protected InventoryOpenEvent openEvent;
    protected boolean hidden;

}
