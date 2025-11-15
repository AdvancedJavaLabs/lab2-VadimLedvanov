package tasks;

import aggregators.ResultAggregator;
import config.RabbitMQConfig;

public class ResultAggregatorTask implements Runnable, AutoCloseable {

    private final RabbitMQConfig config;
    private ResultAggregator aggregator;

    public ResultAggregatorTask(RabbitMQConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        try {
            aggregator = new ResultAggregator(config);
            aggregator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (aggregator != null) {
            aggregator.stop();
        }
    }
}