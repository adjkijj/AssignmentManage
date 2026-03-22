package model;

import java.util.Date;

/**
 * Submission model representing a student's file submission for an assignment.
 * Extended: version control, status tracking, late flag, group support.
 */
public class Submission {
    private int submissionId;
    private int assignmentId;
    private int studentId;
    private String filePath;
    private Date submittedAt;
    private Double grade;
    private String feedback;
    private Date gradedAt;
    private int version;
    private String status;    // submitted, late, graded
    private boolean isLate;
    private Integer groupId;  // nullable - for group assignments
    private String studentName;    // joined field
    private String assignmentTitle; // joined field

    public Submission() {
        this.version = 1;
        this.status = "submitted";
        this.isLate = false;
    }

    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Date getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Date getGradedAt() { return gradedAt; }
    public void setGradedAt(Date gradedAt) { this.gradedAt = gradedAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isIsLate() { return isLate; }
    public void setIsLate(boolean isLate) { this.isLate = isLate; }

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }
}
