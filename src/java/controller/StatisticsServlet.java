package controller;

import dao.SubmissionDAO;
import dao.AssignmentDAO;
import model.Assignment;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Feature 8: Grade Statistics Dashboard
 * /statistics
 */
public class StatisticsServlet extends HttpServlet {
    private SubmissionDAO submissionDAO = new SubmissionDAO();
    private AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"instructor".equals(currentUser.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        if ("api".equals(action)) {
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            Map<String, Integer> dist = submissionDAO.getGradeDistribution(assignmentId);
            
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Integer> entry : dist.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
                first = false;
            }
            json.append("}");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } else {
            try {
                int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                Assignment a = assignmentDAO.getAssignmentById(assignmentId);
                if (a == null) {
                    response.sendRedirect(request.getContextPath() + "/assignments");
                    return;
                }
                request.setAttribute("assignment", a);
                request.getRequestDispatcher("/instructor/statistics.jsp").forward(request, response);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/assignments");
            }
        }
    }
}
