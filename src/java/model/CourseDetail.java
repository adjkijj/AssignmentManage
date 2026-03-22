package model;

import java.util.List;

/**
 * DTO for comprehensive Course Detail view in Admin Dashboard.
 */
public class CourseDetail {
    private Course course;
    private List<User> enrolledStudents;
    private List<AssignmentStat> assignmentStats;

    public static class AssignmentStat {
        private Assignment assignment;
        private int submissionCount;

        public AssignmentStat(Assignment assignment, int submissionCount) {
            this.assignment = assignment;
            this.submissionCount = submissionCount;
        }

        public Assignment getAssignment() { return assignment; }
        public void setAssignment(Assignment assignment) { this.assignment = assignment; }
        public int getSubmissionCount() { return submissionCount; }
        public void setSubmissionCount(int submissionCount) { this.submissionCount = submissionCount; }
    }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public List<User> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(List<User> enrolledStudents) { this.enrolledStudents = enrolledStudents; }

    public List<AssignmentStat> getAssignmentStats() { return assignmentStats; }
    public void setAssignmentStats(List<AssignmentStat> assignmentStats) { this.assignmentStats = assignmentStats; }
}
