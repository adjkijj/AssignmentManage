package controller;

import dao.AnnouncementDAO;
import dao.CourseDAO;
import model.Course;
import model.Announcement;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class AnnouncementServlet extends HttpServlet {
    private AnnouncementDAO adao = new AnnouncementDAO();
    private CourseDAO cdao = new CourseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if ("instructor".equals(currentUser.getRole())) {
            List<Announcement> list = adao.getByInstructor(currentUser.getUserId());
            List<Course> courses = cdao.getCoursesByInstructor(currentUser.getUserId());
            request.setAttribute("announcements", list);
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("/instructor/announcements.jsp").forward(request, response);
        } else if ("student".equals(currentUser.getRole())) {
            List<Announcement> list = adao.getByStudent(currentUser.getUserId());
            request.setAttribute("announcements", list);
            request.getRequestDispatcher("/student/announcements.jsp").forward(request, response);
        } else if ("admin".equals(currentUser.getRole())) {
            List<Announcement> list = adao.getGlobalAnnouncements();
            request.setAttribute("announcements", list);
            request.getRequestDispatcher("/admin/announcements.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        boolean isInstructor = "instructor".equals(currentUser.getRole());
        boolean isAdmin = "admin".equals(currentUser.getRole());
        
        if (!isInstructor && !isAdmin) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            int courseId = 0;
            if (isInstructor) {
                try { courseId = Integer.parseInt(request.getParameter("courseId")); } catch(Exception e) {}
            }
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            String targetRole = request.getParameter("targetRole");
            if (targetRole == null || targetRole.trim().isEmpty()) {
                targetRole = "all";
            }

            Announcement a = new Announcement();
            a.setCourseId(courseId);
            a.setInstructorId(currentUser.getUserId());
            a.setTitle(title);
            a.setContent(content);
            a.setTargetRole(targetRole);
            adao.insert(a);
            
            dao.NotificationDAO ndao = new dao.NotificationDAO();
            if (courseId == 0) {
                if ("instructor".equals(targetRole)) {
                    ndao.notifyAllInstructors("Hệ Thống (Giảng Viên): " + title, "Có thông báo hệ thống mới dành cho Giảng viên.");
                } else {
                    ndao.notifyAllUsers("Hệ Thống: " + title, "Có thông báo hệ thống mới từ Quản trị viên.");
                }
            } else {
                ndao.notifyStudentsInCourse(courseId, "Thông Báo: " + title, "Có thông báo mới cho lớp học của bạn.");
            }
            
            response.sendRedirect(request.getContextPath() + "/announcements?success=Added");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            adao.delete(id, currentUser.getUserId());
            response.sendRedirect(request.getContextPath() + "/announcements?success=Deleted");
        }
    }
}
