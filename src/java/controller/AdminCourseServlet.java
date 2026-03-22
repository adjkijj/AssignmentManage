package controller;

import dao.CourseDAO;
import dao.SemesterDAO;
import dao.SubjectDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import java.io.IOException;

/**
 * AdminCourseServlet - Handles course/class management for administrators.
 * Updated: supports semester_id, subject_id, max_students fields.
 */
public class AdminCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        CourseDAO courseDAO = new CourseDAO();
        UserDAO userDAO = new UserDAO();

        switch (action) {
            case "toggleActive":
                int courseId = Integer.parseInt(request.getParameter("id"));
                courseDAO.toggleActive(courseId);
                
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_COURSE", "Course", courseId, "Toggled active status");
                
                response.sendRedirect(request.getContextPath() + "/admin/courses");
                return;

            case "edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                Course editCourse = courseDAO.getCourseById(editId);
                request.setAttribute("editCourse", editCourse);
                break;
        }

        request.setAttribute("courses", courseDAO.getAllCourses());
        request.setAttribute("instructors", userDAO.getUsersByRole("instructor"));
        request.setAttribute("semesters", new SemesterDAO().getActiveSemesters());
        request.setAttribute("subjects", new SubjectDAO().getAllSubjects());
        request.getRequestDispatcher("/admin/courses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        CourseDAO courseDAO = new CourseDAO();

        if ("create".equals(action)) {
            Course course = buildCourseFromRequest(request);
            if (courseDAO.createCourse(course)) {
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "CREATE_COURSE", "Course", null, "Created course: " + course.getCourseCode());
                request.setAttribute("success", "Class created successfully.");
            } else {
                request.setAttribute("error", "Failed to create class. Course code may already exist.");
            }
        } else if ("edit".equals(action)) {
            Course course = buildCourseFromRequest(request);
            course.setCourseId(Integer.parseInt(request.getParameter("courseId")));
            if (courseDAO.updateCourse(course)) {
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_COURSE", "Course", course.getCourseId(), "Updated course: " + course.getCourseCode());
                request.setAttribute("success", "Class updated successfully.");
            } else {
                request.setAttribute("error", "Failed to update class.");
            }
        } else if ("delete".equals(action)) {
            try {
                int courseId = Integer.parseInt(request.getParameter("courseId"));
                courseDAO.deleteCourse(courseId);
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "DELETE_COURSE", "Course", courseId, "Deleted course");
                request.setAttribute("success", "Class deleted successfully.");
            } catch (Exception e) {
                request.setAttribute("error", "Cannot delete this class because it has enrolled students, assignments, group data, or grade records.");
            }
        }

        request.setAttribute("courses", courseDAO.getAllCourses());
        request.setAttribute("instructors", new UserDAO().getUsersByRole("instructor"));
        request.setAttribute("semesters", new SemesterDAO().getActiveSemesters());
        request.setAttribute("subjects", new SubjectDAO().getAllSubjects());
        request.getRequestDispatcher("/admin/courses.jsp").forward(request, response);
    }

    private Course buildCourseFromRequest(HttpServletRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getParameter("courseCode").trim());
        course.setCourseName(request.getParameter("courseName").trim());
        course.setDescription(request.getParameter("description"));
        course.setInstructorId(Integer.parseInt(request.getParameter("instructorId")));
        String semId = request.getParameter("semesterId");
        if (semId != null && !semId.isEmpty()) course.setSemesterId(Integer.parseInt(semId));
        String subId = request.getParameter("subjectId");
        if (subId != null && !subId.isEmpty()) course.setSubjectId(Integer.parseInt(subId));
        String maxSt = request.getParameter("maxStudents");
        if (maxSt != null && !maxSt.isEmpty()) course.setMaxStudents(Integer.parseInt(maxSt));
        else course.setMaxStudents(40);
        return course;
    }
}
