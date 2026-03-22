package model;

import java.util.List;
import java.util.Map;

public class DashboardData {
    private int totalUsers;
    private int totalCourses;
    private int totalSubmissions;
    private int submissionsToday;
    private int activeCoursesThisSemester;
    
    // Using List<Map<String, Object>> to flexibly hold generic row data
    private List<Map<String, Object>> recentLogins;
    private List<Map<String, Object>> recentSubmissions;
    private List<Map<String, Object>> submissionTrend;
    private List<Map<String, Object>> topCourses;

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public int getSubmissionsToday() { return submissionsToday; }
    public void setSubmissionsToday(int submissionsToday) { this.submissionsToday = submissionsToday; }

    public int getActiveCoursesThisSemester() { return activeCoursesThisSemester; }
    public void setActiveCoursesThisSemester(int activeCoursesThisSemester) { this.activeCoursesThisSemester = activeCoursesThisSemester; }

    public List<Map<String, Object>> getRecentLogins() { return recentLogins; }
    public void setRecentLogins(List<Map<String, Object>> recentLogins) { this.recentLogins = recentLogins; }

    public List<Map<String, Object>> getRecentSubmissions() { return recentSubmissions; }
    public void setRecentSubmissions(List<Map<String, Object>> recentSubmissions) { this.recentSubmissions = recentSubmissions; }

    public List<Map<String, Object>> getSubmissionTrend() { return submissionTrend; }
    public void setSubmissionTrend(List<Map<String, Object>> submissionTrend) { this.submissionTrend = submissionTrend; }

    public List<Map<String, Object>> getTopCourses() { return topCourses; }
    public void setTopCourses(List<Map<String, Object>> topCourses) { this.topCourses = topCourses; }
}
