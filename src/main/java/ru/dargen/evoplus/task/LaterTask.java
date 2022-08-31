package ru.dargen.evoplus.task;

import java.util.function.Consumer;

public class LaterTask extends BaseTask {

    protected final long createdTick = System.currentTimeMillis();

    protected final long delay;

    public LaterTask(long delay, boolean sync, Consumer<Task> handler) {
        super(sync, handler);
        this.delay = delay;
    }

    @Override
    public void tick(long currentTick) {
        if (createdTick + delay * 50 >= currentTick) {
            handler.accept(this);
            cancel();
        }
    }

}
