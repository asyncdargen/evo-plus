package ru.dargen.evoplus.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import ru.dargen.evoplus.event.CancellableEvent;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class InventoryFillEvent extends CancellableEvent {

    protected int syncId;
    protected List<ItemStack> contents;
    protected InventoryOpenEvent openEvent;
    protected ScreenHandler screenHandler;
    protected boolean hidden;

}
