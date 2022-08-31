package ru.dargen.evoplus.task;

import java.util.function.Consumer;

public interface Task {

    int getId();

    boolean isSync();

    Consumer<Task> getHandler();

    void tick(long currentTick);

    boolean isCancelled();

    boolean cancel();

}
