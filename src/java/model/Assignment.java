package model;

import java.util.Date;

/**
 * Assignment model representing an assignment posted by an instructor.
 * Extended: soft_deadline and hard_deadline for advanced deadline logic.
 */
public class Assignment {
    private int assignmentId;
    private int courseId;
    private int instructorId;
    private String title;
    private String description;
    private Date deadline;
    private Date softDeadline;
    private Date hardDeadline;
    private String attachmentPath;
    private Date createdAt;
    private String courseName; // joined field

    public Assignment() {}

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public Date getSoftDeadline() { return softDeadline; }
    public void setSoftDeadline(Date softDeadline) { this.softDeadline = softDeadline; }

    public Date getHardDeadline() { return hardDeadline; }
    public void setHardDeadline(Date hardDeadline) { this.hardDeadline = hardDeadline; }

    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    // Transient fields for student views (Features 1 & 6)
    private String submissionStatus; // null=not submitted, submitted, graded, late
    private Double gradeValue;       // grade from submission
    private String courseCode;       // for display

    public String getSubmissionStatus() { return submissionStatus; }
    public void setSubmissionStatus(String submissionStatus) { this.submissionStatus = submissionStatus; }
    public Double getGradeValue() { return gradeValue; }
    public void setGradeValue(Double gradeValue) { this.gradeValue = gradeValue; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    // Transient field for instructor views (Feature 2)
    private int pendingCount; // number of ungraded submissions
    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
}
