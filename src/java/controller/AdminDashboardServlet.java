package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DashboardData;
import model.User;
import service.DashboardService;

import java.io.IOException;

public class AdminDashboardServlet extends HttpServlet {

    private DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        DashboardData data;
        try {
            data = dashboardService.buildDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            data = new DashboardData();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        topJson.append("]");
        request.setAttribute("topCoursesJson", topJson.toString());

        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
