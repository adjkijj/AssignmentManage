package controller;

import dao.CourseDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;

/**
 * InstructorCourseServlet - Displays the classes managed by the instructor.
 */
public class InstructorCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        
        if (currentUser == null || !"instructor".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CourseDAO courseDAO = new CourseDAO();
        request.setAttribute("myCourses", courseDAO.getCoursesByInstructor(currentUser.getUserId()));
        
        request.getRequestDispatcher("/instructor/courses.jsp").forward(request, response);
    }
}
