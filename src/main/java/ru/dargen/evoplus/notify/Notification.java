package ru.dargen.evoplus.notify;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.evoplus.util.Util;

import java.awt.*;

@Data
public class Notification {

    protected String name;
    protected int x, y;
    protected long duration, startTime = System.currentTimeMillis();
    protected boolean enabled;
    protected final String message;
    protected final Type type;

    public Notification(Type type, String text, int duration, String message) {
        this.message = message;
        this.name = text;
        this.type = type;
        this.duration = duration;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {

        ERROR(Color.RED.getRGB()),
        INFO(Util.rgb(0, 140, 195)),
        CONFIRM(Color.GREEN.getRGB()),
        WARN(Color.YELLOW.getRGB()),
        ;

        public final int color;

    }
}
