package ru.dargen.evoplus.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.dargen.evoplus.event.CancellableEvent;

@Setter @Getter
@AllArgsConstructor
public class ChatSendEvent extends CancellableEvent {

    protected String text;

    public boolean isCommand() {
        return text.startsWith("/");
    }

}
