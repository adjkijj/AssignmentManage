package controller;

import service.CourseDetailService;
import model.CourseDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling Admin Course Detail page.
 */
public class CourseDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/courses");
            return;
        }
        try {
            int courseId = Integer.parseInt(idStr);
            CourseDetailService service = new CourseDetailService();
            CourseDetail detail = service.getCourseDetail(courseId);
            if (detail == null || detail.getCourse() == null) {
                response.sendRedirect(request.getContextPath() + "/admin/courses");
                return;
            }
            request.setAttribute("detail", detail);
            request.getRequestDispatcher("/admin/course-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/courses");
        }
    }
}
