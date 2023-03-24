package ru.dargen.evoplus.task;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class TaskBus {

    private final Thread thread;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final EvoPlus mod;

    public TaskBus(EvoPlus mod) {
        this.mod = mod;
        thread = new Thread(this::run);
        thread.setName("TaskBus-Thread");
        thread.start();
    }

    public Task run(long delay, long period, Consumer<Task> handler) {
        val task = new ScheduledTask(delay, period, true, handler);

        tasks.put(task.getId(), task);
        return task;
    }

    public Task runAsync(long delay, long period, Consumer<Task> handler) {
        val task = new ScheduledTask(delay, period, false, handler);

        tasks.put(task.getId(), task);
        return task;
    }

    public Task runLater(long delay, Consumer<Task> handler) {
        val task = new LaterTask(delay, true, handler);

        tasks.put(task.getId(), task);
        return task;
    }

    public Task runLaterAsync(long delay, Consumer<Task> handler) {
        val task = new LaterTask(delay, false, handler);

        tasks.put(task.getId(), task);
        return task;
    }

    @SneakyThrows
    private void run() {
        while (!thread.isInterrupted()) {
            val currentTick = System.currentTimeMillis();

            tasks.values().forEach(task -> {
                if (task.isSync()) {
                    try {
                        task.tick(currentTick);
                    } catch (Throwable t) {
                        mod.getLogger().error("Error while running task " + task.getId(), t);
                    }
                } else executor.execute(() -> {
                    try {
                        task.tick(currentTick);
                    } catch (Throwable t) {
                        mod.getLogger().error("Error while running task " + task.getId(), t);
                    }
                });
            });
            Thread.sleep(50);
        }
    }
}
