package dto;

import java.util.List;

public class FinalResult {
    private String jobId;
    private int totalWords;
    private List<WordCountEntry> topN;
    private double averageSentiment;
    private String fullReplacedText;
    private List<String> sortedSentences;

    public FinalResult() {}

    public FinalResult(String jobId,
                       int totalWords,
                       List<WordCountEntry> topN,
                       double averageSentiment,
                       String fullReplacedText,
                       List<String> sortedSentences) {
        this.jobId = jobId;
        this.totalWords = totalWords;
        this.topN = topN;
        this.averageSentiment = averageSentiment;
        this.fullReplacedText = fullReplacedText;
        this.sortedSentences = sortedSentences;
    }

    public String getJobId() {
        return jobId;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public List<WordCountEntry> getTopN() {
        return topN;
    }

    public double getAverageSentiment() {
        return averageSentiment;
    }

    public String getFullReplacedText() {
        return fullReplacedText;
    }

    public List<String> getSortedSentences() {
        return sortedSentences;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public void setTopN(List<WordCountEntry> topN) {
        this.topN = topN;
    }

    public void setAverageSentiment(double averageSentiment) {
        this.averageSentiment = averageSentiment;
    }

    public void setFullReplacedText(String fullReplacedText) {
        this.fullReplacedText = fullReplacedText;
    }

    public void setSortedSentences(List<String> sortedSentences) {
        this.sortedSentences = sortedSentences;
    }
}
