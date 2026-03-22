package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;

/**
 * DashboardServlet - Displays role-based dashboard with statistics.
 * Extended: upcoming deadlines, recent notifications, audit logs.
 */
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String role = currentUser.getRole();

        switch (role) {
            case "student":
                loadStudentDashboard(request, currentUser);
                request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
                break;
            case "instructor":
                loadInstructorDashboard(request, currentUser);
                request.getRequestDispatcher("/instructor/dashboard.jsp").forward(request, response);
                break;
            case "admin":
                loadAdminDashboard(request);
                request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private void loadStudentDashboard(HttpServletRequest request, User user) {
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        AssignmentDAO assignmentDAO = new AssignmentDAO();
        SubmissionDAO submissionDAO = new SubmissionDAO();
        NotificationDAO notificationDAO = new NotificationDAO();

        request.setAttribute("courseCount", enrollmentDAO.countByStudent(user.getUserId()));
        request.setAttribute("assignmentCount", assignmentDAO.countByStudent(user.getUserId()));
        request.setAttribute("submissionCount", submissionDAO.countByStudent(user.getUserId()));
        request.setAttribute("gradedCount", submissionDAO.countGradedByStudent(user.getUserId()));

        // Dashboard extensions: upcoming deadlines + recent notifications
        request.setAttribute("upcomingDeadlines", assignmentDAO.getUpcomingByStudent(user.getUserId()));
        request.setAttribute("recentNotifications", notificationDAO.getRecentByUser(user.getUserId(), 5));
        request.setAttribute("unreadCount", notificationDAO.countUnread(user.getUserId()));

        // Feature 2: Progress bars per course
        request.setAttribute("courseProgress", assignmentDAO.getProgressByStudent(user.getUserId()));

        // Feature 4: Todo list
        request.setAttribute("todoItems", assignmentDAO.getTodoItemsByStudent(user.getUserId()));
    }

    private void loadInstructorDashboard(HttpServletRequest request, User user) {
        CourseDAO courseDAO = new CourseDAO();
        AssignmentDAO assignmentDAO = new AssignmentDAO();
        SubmissionDAO submissionDAO = new SubmissionDAO();
        NotificationDAO notificationDAO = new NotificationDAO();

        request.setAttribute("courses", courseDAO.getCoursesByInstructor(user.getUserId()));
        request.setAttribute("assignmentCount", assignmentDAO.countByInstructor(user.getUserId()));
        request.setAttribute("pendingCount", submissionDAO.countPendingByInstructor(user.getUserId()));
        request.setAttribute("recentNotifications", notificationDAO.getRecentByUser(user.getUserId(), 5));

        // Feature 1: Per-course breakdown
        request.setAttribute("courseStatsList", courseDAO.getCourseStatsByInstructor(user.getUserId()));

        // Feature 12: At-risk students
        request.setAttribute("atRiskList", submissionDAO.getAtRiskStudents(user.getUserId()));
    }

    private void loadAdminDashboard(HttpServletRequest request) {
        service.DashboardService dashboardService = new service.DashboardService();
        model.DashboardData data;
        try {
            data = dashboardService.buildDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            data = new model.DashboardData();
        }
        request.setAttribute("dashboardData", data);

        // Convert trend data to JSON string for Chart.js
        StringBuilder trendJson = new StringBuilder("[");
        try {
            if (data.getSubmissionTrend() != null) {
                for (int i = 0; i < data.getSubmissionTrend().size(); i++) {
                    java.util.Map<String, Object> map = data.getSubmissionTrend().get(i);
                    trendJson.append("{\"date\":\"").append(map.get("date")).append("\",\"count\":").append(map.get("count")).append("}");
                    if (i < data.getSubmissionTrend().size() - 1) trendJson.append(",");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        trendJson.append("]");
        request.setAttribute("trendDataJson", trendJson.toString());

        // Convert top courses to JSON string for Chart.js
        StringBuilder topJson = new StringBuilder("[");
        try {
            if (data.getTopCourses() != null) {
                for (int i = 0; i < data.getTopCourses().size(); i++) {
                    java.util.Map<String, Object> map = data.getTopCourses().get(i);
                    topJson.append("{\"courseCode\":\"").append(map.get("courseCode")).append("\",\"count\":").append(map.get("count")).append("}");
                    if (i < data.getTopCourses().size() - 1) topJson.append(",");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        topJson.append("]");
        request.setAttribute("topCoursesJson", topJson.toString());

        // Legacy attributes for backwards compatibility
        UserDAO userDAO = new UserDAO();
        request.setAttribute("studentCount", userDAO.countByRole("student"));
        request.setAttribute("instructorCount", userDAO.countByRole("instructor"));
        request.setAttribute("recentAuditLogs", new AuditLogDAO().getRecentLogs(10));
    }
}

