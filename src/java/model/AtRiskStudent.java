package model;

import java.util.List;

/**
 * Feature 12: At-risk student DTO.
 */
public class AtRiskStudent {
    private int studentId;
    private String studentName;
    private int courseId;
    private String courseName;
    private List<String> reasons;
    private int daysSinceLastSubmission;

    public AtRiskStudent() {}

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public int getDaysSinceLastSubmission() { return daysSinceLastSubmission; }
    public void setDaysSinceLastSubmission(int daysSinceLastSubmission) { this.daysSinceLastSubmission = daysSinceLastSubmission; }
}
