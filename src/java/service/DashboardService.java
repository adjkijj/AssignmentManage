package service;

import dao.DashboardDAO;
import model.DashboardData;
import java.util.ArrayList;

public class DashboardService {
    
    private DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardData buildDashboard() {
        DashboardData data = new DashboardData();
        
        // Wrap each call individually so one failure doesn't block the rest
        try { data.setTotalUsers(dashboardDAO.countTotalUsers()); } catch (Exception e) { e.printStackTrace(); }
        try { data.setTotalCourses(dashboardDAO.countTotalCourses()); } catch (Exception e) { e.printStackTrace(); }
        try { data.setTotalSubmissions(dashboardDAO.countTotalSubmissions()); } catch (Exception e) { e.printStackTrace(); }
        try { data.setSubmissionsToday(dashboardDAO.countSubmissionsToday()); } catch (Exception e) { e.printStackTrace(); }
        try { data.setActiveCoursesThisSemester(dashboardDAO.countActiveCoursesThisSemester()); } catch (Exception e) { e.printStackTrace(); }
        
        try { data.setRecentLogins(dashboardDAO.getRecentLogins(10)); } catch (Exception e) { data.setRecentLogins(new ArrayList<>()); e.printStackTrace(); }
        try { data.setRecentSubmissions(dashboardDAO.getRecentSubmissions(10)); } catch (Exception e) { data.setRecentSubmissions(new ArrayList<>()); e.printStackTrace(); }
        try { data.setSubmissionTrend(dashboardDAO.getSubmissionTrend(14)); } catch (Exception e) { data.setSubmissionTrend(new ArrayList<>()); e.printStackTrace(); }
        try { data.setTopCourses(dashboardDAO.getTopCoursesBySubmission(5)); } catch (Exception e) { data.setTopCourses(new ArrayList<>()); e.printStackTrace(); }
        
        return data;
    }
}
