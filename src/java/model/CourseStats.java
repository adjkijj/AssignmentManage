package model;

import java.util.Date;

/**
 * DTO for instructor dashboard — per-course statistics.
 */
public class CourseStats {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int totalAssignments;
    private int pendingGrading;
    private int totalStudents;
    private Date nearestDeadline;

    public CourseStats() {}

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(int totalAssignments) { this.totalAssignments = totalAssignments; }

    public int getPendingGrading() { return pendingGrading; }
    public void setPendingGrading(int pendingGrading) { this.pendingGrading = pendingGrading; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public Date getNearestDeadline() { return nearestDeadline; }
    public void setNearestDeadline(Date nearestDeadline) { this.nearestDeadline = nearestDeadline; }
}
