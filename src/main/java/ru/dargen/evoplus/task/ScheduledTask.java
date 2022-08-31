package ru.dargen.evoplus.task;

import java.util.function.Consumer;

public class ScheduledTask extends BaseTask {

    protected long lastTick = 0;

    protected long delay;
    protected final long period;

    public ScheduledTask(long delay, long period, boolean sync, Consumer<Task> handler) {
        super(sync, handler);
        this.delay = delay;
        this.period = period;
    }

    @Override
    public void tick(long currentTick) {
        if (delay <= 0) {
            if (lastTick + period * 50 <= currentTick) {
                lastTick = currentTick;
                handler.accept(this);
            }
        } else delay--;
    }

}
