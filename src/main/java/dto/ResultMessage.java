package dto;

public class ResultMessage {
    private String jobId;
    private int sectionId;
    private int totalSections;
    private ResultPayload payload;

    public ResultMessage() {}

    public ResultMessage(String jobId, int sectionId, int totalSections, ResultPayload payload) {
        this.jobId = jobId;
        this.sectionId = sectionId;
        this.totalSections = totalSections;
        this.payload = payload;
    }

    public String getJobId() {
        return jobId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public ResultPayload getPayload() {
        return payload;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }

    public void setPayload(ResultPayload payload) {
        this.payload = payload;
    }
}

