package model;

/**
 * DTO for submission rate statistics per assignment — Feature 3.
 */
public class SubmissionStats {
    private int totalEnrolled;
    private int submittedOnTime;
    private int submittedLate;
    private int notSubmitted;
    private int graded;

    public SubmissionStats() {}

    public int getTotalEnrolled() { return totalEnrolled; }
    public void setTotalEnrolled(int totalEnrolled) { this.totalEnrolled = totalEnrolled; }

    public int getSubmittedOnTime() { return submittedOnTime; }
    public void setSubmittedOnTime(int submittedOnTime) { this.submittedOnTime = submittedOnTime; }

    public int getSubmittedLate() { return submittedLate; }
    public void setSubmittedLate(int submittedLate) { this.submittedLate = submittedLate; }

    public int getNotSubmitted() { return notSubmitted; }
    public void setNotSubmitted(int notSubmitted) { this.notSubmitted = notSubmitted; }

    public int getGraded() { return graded; }
    public void setGraded(int graded) { this.graded = graded; }

    public int getTotalSubmitted() { return submittedOnTime + submittedLate; }

    public int getOnTimePercent() {
        return totalEnrolled > 0 ? (submittedOnTime * 100 / totalEnrolled) : 0;
    }
    public int getLatePercent() {
        return totalEnrolled > 0 ? (submittedLate * 100 / totalEnrolled) : 0;
    }
    public int getNotSubmittedPercent() {
        return totalEnrolled > 0 ? (notSubmitted * 100 / totalEnrolled) : 0;
    }
}
