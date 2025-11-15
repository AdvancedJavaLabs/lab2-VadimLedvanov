package dto;

import java.util.List;
import java.util.Map;

public class ResultPayload {
    private int wordCount;
    private Map<String, Integer> wordFreq;
    private double sentimentScore;
    private String replacedText;
    private List<String> sortedSentences;

    public ResultPayload() {}

    public ResultPayload(int wordCount,
                         Map<String, Integer> wordFreq,
                         double sentimentScore,
                         String replacedText,
                         List<String> sortedSentences) {
        this.wordCount = wordCount;
        this.wordFreq = wordFreq;
        this.sentimentScore = sentimentScore;
        this.replacedText = replacedText;
        this.sortedSentences = sortedSentences;
    }

    public int getWordCount() {
        return wordCount;
    }

    public Map<String, Integer> getWordFreq() {
        return wordFreq;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public String getReplacedText() {
        return replacedText;
    }

    public List<String> getSortedSentences() {
        return sortedSentences;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setWordFreq(Map<String, Integer> wordFreq) {
        this.wordFreq = wordFreq;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public void setReplacedText(String replacedText) {
        this.replacedText = replacedText;
    }

    public void setSortedSentences(List<String> sortedSentences) {
        this.sortedSentences = sortedSentences;
    }
}

