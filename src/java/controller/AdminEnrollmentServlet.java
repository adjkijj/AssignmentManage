package controller;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.UserDAO;
import model.Enrollment;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AdminEnrollmentServlet - Handles enrollment management for administrators.
 * Actions: list, enroll, unenroll
 */
public class AdminEnrollmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        if ("unenroll".equals(action)) {
            int enrollmentId = Integer.parseInt(request.getParameter("id"));
            enrollmentDAO.unenroll(enrollmentId);
            response.sendRedirect(request.getContextPath() + "/admin/enrollments");
            return;
        }

        UserDAO userDAO = new UserDAO();
        CourseDAO courseDAO = new CourseDAO();

        String keyword = request.getParameter("keyword");
        String courseFilter = request.getParameter("courseFilter");
        
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (NumberFormatException e) { page = 1; }
        }
        
        service.EnrollmentService searchService = new service.EnrollmentService();
        java.util.List<Enrollment> enrollments = searchService.search(keyword, courseFilter, page, pageSize);
        int totalPages = searchService.getTotalPages(keyword, courseFilter, pageSize);
        int totalRecords = searchService.getTotalRecords(keyword, courseFilter);
        
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("keyword", keyword);
        request.setAttribute("courseFilter", courseFilter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("students", userDAO.getUsersByRole("student"));
        request.setAttribute("courses", courseDAO.getAllCourses());
        request.getRequestDispatcher("/admin/enrollments.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        if ("enroll".equals(action)) {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));

            if (enrollmentDAO.isEnrolled(studentId, courseId)) {
                request.setAttribute("error", "Student is already enrolled in this course.");
            } else {
                if (enrollmentDAO.enroll(studentId, courseId)) {
                    request.setAttribute("success", "Student enrolled successfully.");
                } else {
                    request.setAttribute("error", "Failed to enroll student.");
                }
            }
        } else if ("enrollAll".equals(action)) {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int count = enrollmentDAO.enrollAllStudents(courseId);
            if (count > 0) {
                request.setAttribute("success", count + " student(s) enrolled successfully.");
            } else {
                request.setAttribute("error", "All students are already enrolled in this course (or no students exist).");
            }
        }

        UserDAO userDAO = new UserDAO();
        CourseDAO courseDAO = new CourseDAO();

        service.EnrollmentService searchService = new service.EnrollmentService();
        request.setAttribute("enrollments", searchService.search(null, null, 1, 10));
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", searchService.getTotalPages(null, null, 10));
        request.setAttribute("totalRecords", searchService.getTotalRecords(null, null));
        request.setAttribute("pageSize", 10);
        request.setAttribute("students", userDAO.getUsersByRole("student"));
        request.setAttribute("courses", courseDAO.getAllCourses());
        request.getRequestDispatcher("/admin/enrollments.jsp").forward(request, response);
    }
}
