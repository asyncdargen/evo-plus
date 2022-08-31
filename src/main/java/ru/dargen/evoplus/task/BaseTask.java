package ru.dargen.evoplus.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.dargen.evoplus.EvoPlus;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public abstract class BaseTask implements Task {

    private static final AtomicInteger ID = new AtomicInteger();

    protected final int id = ID.incrementAndGet();
    protected final boolean sync;
    protected final Consumer<Task> handler;

    @Override
    public boolean isCancelled() {
        return !EvoPlus.instance().getTaskBus().getTasks().containsKey(id);
    }

    @Override
    public boolean cancel() {
        if (isCancelled()) return false;

        EvoPlus.instance().getTaskBus().getTasks().remove(id);

        return true;
    }

}
