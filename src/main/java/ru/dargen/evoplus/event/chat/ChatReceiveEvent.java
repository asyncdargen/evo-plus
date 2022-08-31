package ru.dargen.evoplus.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.text.Text;
import ru.dargen.evoplus.event.CancellableEvent;

@Getter @Setter
@AllArgsConstructor
public class ChatReceiveEvent extends CancellableEvent {

    protected Text text;

    public void setText(String text) {
        this.text = Text.of(text);
    }

}
