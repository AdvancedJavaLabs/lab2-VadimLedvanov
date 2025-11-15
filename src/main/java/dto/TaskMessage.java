package dto;

public class TaskMessage {
    private String jobId;
    private int sectionId;
    private int totalSections;
    private String section;

    public TaskMessage() {}

    public TaskMessage(String jobId, int sectionId, int totalSections, String section) {
        this.jobId = jobId;
        this.sectionId = sectionId;
        this.totalSections = totalSections;
        this.section = section;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "TaskMessage{" +
                "jobId='" + jobId + '\'' +
                ", sectionId=" + sectionId +
                ", totalSections=" + totalSections +
                ", section='" + section + '\'' +
                '}';
    }
}
