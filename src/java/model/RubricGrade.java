package model;

public class RubricGrade {
    private int id;
    private int submissionId;
    private int criteriaId;
    private double pointsEarned;
    private String comment;

    public RubricGrade() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }
    public int getCriteriaId() { return criteriaId; }
    public void setCriteriaId(int criteriaId) { this.criteriaId = criteriaId; }
    public double getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(double pointsEarned) { this.pointsEarned = pointsEarned; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
