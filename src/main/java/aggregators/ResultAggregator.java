package aggregators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import config.RabbitMQConfig;
import dto.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static config.RabbitMQConfig.*;

public class ResultAggregator {
    private RabbitMQConfig config;
    private Channel channel;
    private String consumerTag;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int TOP_N = 10;

    public ResultAggregator(RabbitMQConfig config) {
        this.config = config;
    }

    private static class JobState {
        int totalSections = -1;
        int receivedSections = 0;
        int totalWords = 0;
        double sentimentSum = 0.0;
        Map<String, Integer> globalFreq = new HashMap<>();
        Map<Integer, String> replacedBySection = new TreeMap<>();
        List<String> allSentences = new ArrayList<>();
    }

    private static final Map<String, JobState> jobs = new HashMap<>();

    public void start() throws Exception {
        channel = config.getConnection().createChannel();

        channel.exchangeDeclare(RESULT_EXCHANGE, "direct", true);
        channel.queueDeclare(RESULT_QUEUE, true, false, false, null);
        channel.queueBind(RESULT_QUEUE, RESULT_EXCHANGE, RESULT_ROUTING_KEY);

        System.out.println("[Aggregator] Waiting for results...");

        DeliverCallback callback = (tag, delivery) -> {
            try {
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                ResultMessage msg = objectMapper.readValue(json, ResultMessage.class);
                System.out.printf("[ResultAggregator] Received message: job=%s section=%d%n",
                        msg.getJobId(), msg.getSectionId());
                handleResult(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        consumerTag = channel.basicConsume(RESULT_QUEUE, true, callback, tag -> {
        });
    }

    private void handleResult(ResultMessage msg) throws Exception {
        String jobId = msg.getJobId();
        JobState state = jobs.computeIfAbsent(jobId, id -> new JobState());

        if (state.totalSections == -1) {
            state.totalSections = msg.getTotalSections();
        }

        ResultPayload p = msg.getPayload();

        state.receivedSections++;
        state.totalWords += p.getWordCount();
        state.sentimentSum += p.getSentimentScore();

        for (var entry : p.getWordFreq().entrySet()) {
            state.globalFreq.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        state.replacedBySection.put(msg.getSectionId(), p.getReplacedText());
        state.allSentences.addAll(p.getSortedSentences());

        System.out.printf("[ResultAggregator] job=%s section=%d (%d/%d)%n",
                jobId, msg.getSectionId(), state.receivedSections, state.totalSections);

        if (state.receivedSections == state.totalSections) {
            finalizeJob(jobId, state);
            jobs.remove(jobId);
        }
    }

    private void finalizeJob(String jobId, JobState state) throws Exception {
        double avgSentiment = state.totalSections > 0
                ? state.sentimentSum / state.totalSections
                : 0.0;

        List<WordCountEntry> topN = state.globalFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(TOP_N)
                .map(e -> new WordCountEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        StringBuilder fullText = new StringBuilder();
        for (var entry : state.replacedBySection.entrySet()) {
            fullText.append(entry.getValue()).append("\n");
        }

        state.allSentences.sort(Comparator.comparingInt(String::length));

        FinalResult result = new FinalResult(
                jobId,
                state.totalWords,
                topN,
                avgSentiment,
                fullText.toString(),
                state.allSentences
        );

        File dir = new File("result");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File out = new File(dir, jobId + ".json");

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, result);

        System.out.printf("[ResultAggregator] Job %s finished. Result saved to %s%n",
                jobId, out.getAbsolutePath());
    }

    public void stop() throws Exception {
        if (channel != null && channel.isOpen()) {
            if (consumerTag != null)
                channel.basicCancel(consumerTag);
            channel.close();
        }
        System.out.println("Aggregator stopped.");
    }
}