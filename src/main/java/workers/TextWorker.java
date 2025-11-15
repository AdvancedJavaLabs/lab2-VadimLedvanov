package workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import config.RabbitMQConfig;
import dto.ResultMessage;
import dto.ResultPayload;
import dto.TaskMessage;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.RabbitMQConfig.*;

public class TextWorker {
    private final RabbitMQConfig config;

    private Channel channel;

    private String consumerTag;

    private String threadName;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> POSITIVE_WORDS = Set.of("good", "great", "excellent", "happy", "love", "nice");

    private static final Set<String> NEGATIVE_WORDS = Set.of("bad", "terrible", "sad", "hate", "awful", "poor");

    private static final Pattern WORD_PATTERN = Pattern.compile("\\p{L}+");

    public TextWorker(RabbitMQConfig config) {
        this.config = config;
    }

    public void start() throws Exception {
        channel = config.getConnection().createChannel();
        threadName = Thread.currentThread().getName();

        channel.exchangeDeclare(TASK_EXCHANGE, "direct", true);
        channel.exchangeDeclare(RESULT_EXCHANGE, "direct", true);

        channel.queueDeclare(TASK_QUEUE, true, false, false, null);
        channel.queueBind(TASK_QUEUE, TASK_EXCHANGE, TASK_ROUTING_KEY);


        System.out.printf("[%s] Waiting for tasks...%n", threadName);

        Channel finalChannel = channel;
        DeliverCallback callback = (tag, delivery) -> {
            try {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                TaskMessage task = objectMapper.readValue(json, TaskMessage.class);
                System.out.printf("[%s] Received task job=%s section=%d%n",
                        threadName,
                        task.getJobId(),
                        task.getSectionId());

                ResultPayload payload = processSection(task.getSection());
                ResultMessage result = new ResultMessage(
                        task.getJobId(),
                        task.getSectionId(),
                        task.getTotalSections(),
                        payload
                );

                System.out.printf("[%s] Sending result for job=%s section=%d%n",
                        threadName,
                        task.getJobId(),
                        task.getSectionId());
                byte[] out = objectMapper.writeValueAsBytes(result);
                finalChannel.basicPublish(RESULT_EXCHANGE, RESULT_ROUTING_KEY, null, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        consumerTag = channel.basicConsume(TASK_QUEUE, true, callback, tag -> {});
    }

    private ResultPayload processSection(String text) {
        Map<String, Integer> freq = new HashMap<>();
        int wordCount = 0;
        int pos = 0;
        int neg = 0;

        Matcher m = WORD_PATTERN.matcher(text.toLowerCase());
        while (m.find()) {
            String word = m.group();
            wordCount++;
            freq.merge(word, 1, Integer::sum);
            if (POSITIVE_WORDS.contains(word)) pos++;
            if (NEGATIVE_WORDS.contains(word)) neg++;
        }

        double sentiment = 0.0;
        if (wordCount > 0) {
            sentiment = (pos - neg) / (double) wordCount;
        }

        String replaced = replaceNames(text);
        List<String> sortedSentences = sortSentencesByLength(text);

        return new ResultPayload(wordCount, freq, sentiment, replaced, sortedSentences);
    }

    private String replaceNames(String text) {
        return text.replaceAll("\\b[A-Z][a-z]{2,}\\b", "NAME");
    }

    private List<String> sortSentencesByLength(String text) {
        String[] sentences = text.split("(?<=[.!?])\\s+");
        List<String> list = new ArrayList<>(Arrays.asList(sentences));
        list.removeIf(String::isBlank);
        list.sort(Comparator.comparingInt(String::length));
        return list;
    }

    public void stop() throws Exception {
        if (channel != null && channel.isOpen()) {
            if (consumerTag != null)
                channel.basicCancel(consumerTag);
            channel.close();
        }

        System.out.printf("%s stopped.%n", threadName);
    }
}
