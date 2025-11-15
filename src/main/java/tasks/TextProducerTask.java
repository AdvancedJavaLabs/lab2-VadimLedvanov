package tasks;

import config.RabbitMQConfig;
import producers.TextProducer;

public class TextProducerTask implements Runnable, AutoCloseable {

    private final String filePath;

    private final RabbitMQConfig config;

    private TextProducer textProducer;

    public TextProducerTask(String filePath, RabbitMQConfig config) {
        this.filePath = filePath;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            textProducer = new TextProducer(config);
            textProducer.start(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        textProducer.stop();
    }
}
