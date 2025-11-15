package tasks;

public class NamedTask implements Runnable {

    private final String name;
    private final Runnable task;

    public NamedTask(String name, Runnable task) {
        this.name = name;
        this.task = task;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(name);
        task.run();
    }
}
