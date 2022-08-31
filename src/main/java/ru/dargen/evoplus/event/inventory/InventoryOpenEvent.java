package ru.dargen.evoplus.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import ru.dargen.evoplus.event.CancellableEvent;

@Getter @Setter
@AllArgsConstructor
public class InventoryOpenEvent extends CancellableEvent {

    protected int syncId;
    protected ScreenHandlerType<?> screenHandlerType;
    protected Text name;
    protected boolean hidden;

    public String getNameString() {
        return name.getString();
    }

    public void setName(String name) {
        this.name = Text.of(name);
    }

}
