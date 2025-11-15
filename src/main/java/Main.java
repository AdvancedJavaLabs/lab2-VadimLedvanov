import config.RabbitMQConfig;
import tasks.NamedTask;
import tasks.ResultAggregatorTask;
import tasks.TextProducerTask;
import tasks.TextWorkerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        RabbitMQConfig config = new RabbitMQConfig();
        List<AutoCloseable> closeables = new ArrayList<>();

        int workersCount = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(2 + workersCount);

        ResultAggregatorTask resultAggregatorTask = new ResultAggregatorTask(config);
        closeables.add(resultAggregatorTask);
        executor.submit(new NamedTask("ResultAggregator", resultAggregatorTask));

        for (int i = 1; i <= workersCount; i++) {
            TextWorkerTask textWorkerTask = new TextWorkerTask(config);
            closeables.add(textWorkerTask);
            executor.submit(new NamedTask("TextWorker-" + i, textWorkerTask));
        }

        TextProducerTask textProducerTask = new TextProducerTask("text.txt", config);
        closeables.add(textProducerTask);
        Future<?> producerFuture = executor.submit(new NamedTask("TextProducer", textProducerTask));

        producerFuture.get();

        Thread.sleep(3000);

        for (AutoCloseable c : closeables) {
            try {
                c.close();
            } catch (Exception e) {
                System.err.println("Error closing: " + e.getMessage());
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        config.close();

        System.out.println("All tasks finished.");
    }
}
