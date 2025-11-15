package tasks;

import config.RabbitMQConfig;
import workers.TextWorker;

public class TextWorkerTask implements Runnable, AutoCloseable {

    private final RabbitMQConfig config;
    private TextWorker textWorker;

    public TextWorkerTask(RabbitMQConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        try {
            textWorker = new TextWorker(config);
            textWorker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        textWorker.stop();
    }
}
