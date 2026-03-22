package controller;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;

/**
 * StudentEnrollServlet - Allows students to browse open classes and self-enroll.
 */
public class StudentEnrollServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        CourseDAO courseDAO = new CourseDAO();
        request.setAttribute("openClasses", courseDAO.getOpenClassesForEnrollment(currentUser.getUserId()));
        request.getRequestDispatcher("/student/enroll.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        int courseId = Integer.parseInt(request.getParameter("courseId"));

        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        int result = enrollmentDAO.selfEnroll(currentUser.getUserId(), courseId);

        switch (result) {
            case 1:
                request.getSession().setAttribute("success", "Successfully enrolled in the class!");
                break;
            case 0:
                request.getSession().setAttribute("error", "You are already enrolled in this class.");
                break;
            case -1:
                request.getSession().setAttribute("error", "This class is full. Cannot enroll.");
                break;
            default:
                request.getSession().setAttribute("error", "Enrollment failed. Please try again.");
                break;
        }

        response.sendRedirect(request.getContextPath() + "/student/enroll");
    }
}
