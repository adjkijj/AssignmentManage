package controller;

import dao.CourseDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;

/**
 * StudentCourseServlet - Displays the classes a student is currently enrolled in.
 */
public class StudentCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        
        if (currentUser == null || !"student".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CourseDAO courseDAO = new CourseDAO();
        request.setAttribute("myCourses", courseDAO.getCoursesByStudent(currentUser.getUserId()));
        
        request.getRequestDispatcher("/student/courses.jsp").forward(request, response);
    }
}
