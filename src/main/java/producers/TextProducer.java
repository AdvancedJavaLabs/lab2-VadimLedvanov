package producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import dto.TaskMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static config.RabbitMQConfig.*;

public class TextProducer {

    private final RabbitMQConfig config;
    private Channel channel;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TextProducer(RabbitMQConfig config) {
        this.config = config;
    }

    public void start(String filePath) throws Exception {

        System.out.println("[TextProducer] Starting");
        String jobId = "job-" + UUID.randomUUID();
        String text = Files.readString(Path.of(filePath));

        channel = config.getConnection().createChannel();
        channel.exchangeDeclare(TASK_EXCHANGE, "direct", true);
        channel.queueDeclare(TASK_QUEUE, true, false, false, null);
        channel.queueBind(TASK_QUEUE, TASK_EXCHANGE, TASK_ROUTING_KEY);

        List<String> sections = splitText(text, 2000);
        int totalSections = sections.size();

        System.out.printf("[TextProducer] Job %s: sending %d sections%n", jobId, totalSections);

        int sectionId = 0;
        for (String section : sections) {
            TaskMessage task = new TaskMessage(jobId, sectionId, totalSections, section);
            byte[] body = objectMapper.writeValueAsBytes(task);

            channel.basicPublish(TASK_EXCHANGE, TASK_ROUTING_KEY, null, body);
            System.out.printf("[TextProducer] Sent section %d/%d%n", sectionId + 1, totalSections);
            sectionId++;
        }
    }

    private List<String> splitText(String text, int chunkSize) {
        List<String> result = new ArrayList<>();
        int length = text.length();
        for (int start = 0; start < length; start += chunkSize) {
            int end = Math.min(start + chunkSize, length);
            result.add(text.substring(start, end));
        }
        return result;
    }

    public void stop() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        if (channel != null) channel.getConnection().close();
        System.out.println("TestProducer stopped.");
    }
}
