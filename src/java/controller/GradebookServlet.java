package controller;

import config.DBContext;
import dao.CourseDAO;
import model.Course;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Feature 7: Gradebook — student × assignment matrix for a course.
 * Access: /gradebook?courseId=X
 */
public class GradebookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"instructor".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        CourseDAO courseDAO = new CourseDAO();
        List<Course> instructorCourses = courseDAO.getCoursesByInstructor(currentUser.getUserId());
        request.setAttribute("instructorCourses", instructorCourses);

        String courseIdStr = request.getParameter("courseId");
        if (courseIdStr == null || courseIdStr.isEmpty()) {
            // Show course picker only
            request.getRequestDispatcher("/instructor/gradebook.jsp").forward(request, response);
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/gradebook");
            return;
        }

        // Ownership check
        boolean owns = false;
        for (Course c : instructorCourses) {
            if (c.getCourseId() == courseId) { owns = true; break; }
        }
        if (!owns) {
            response.sendRedirect(request.getContextPath() + "/gradebook");
            return;
        }

        // Get assignments for this course
        List<Map<String, Object>> assignments = new ArrayList<>();
        // Get students enrolled
        List<Map<String, Object>> students = new ArrayList<>();
        // Build gradebook matrix: Map<studentId, Map<assignmentId, Map<grade/status/isLate/submissionId>>>
        Map<Integer, Map<Integer, Map<String, Object>>> matrix = new LinkedHashMap<>();

        String sqlAssignments = "SELECT assignment_id, title, deadline FROM Assignments WHERE course_id = ? ORDER BY deadline ASC";
        String sqlStudents = "SELECT u.user_id, u.full_name, u.username FROM Users u "
                           + "JOIN Enrollments e ON u.user_id = e.student_id WHERE e.course_id = ? ORDER BY u.full_name";
        String sqlGrades = "SELECT sub.student_id, sub.assignment_id, sub.grade, sub.status, sub.is_late, sub.submission_id "
                         + "FROM Submissions sub JOIN Assignments a ON sub.assignment_id = a.assignment_id "
                         + "WHERE a.course_id = ? AND sub.version = "
                         + "(SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)";

        try (Connection conn = DBContext.getConnection()) {
            // Load assignments
            try (PreparedStatement ps = conn.prepareStatement(sqlAssignments)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> a = new LinkedHashMap<>();
                        a.put("id", rs.getInt("assignment_id"));
                        a.put("title", rs.getString("title"));
                        a.put("deadline", rs.getTimestamp("deadline"));
                        assignments.add(a);
                    }
                }
            }
            // Load students
            try (PreparedStatement ps = conn.prepareStatement(sqlStudents)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> s = new LinkedHashMap<>();
                        int sid = rs.getInt("user_id");
                        s.put("id", sid);
                        s.put("fullName", rs.getString("full_name"));
                        s.put("username", rs.getString("username"));
                        students.add(s);
                        matrix.put(sid, new LinkedHashMap<>());
                    }
                }
            }
            // Load grades
            try (PreparedStatement ps = conn.prepareStatement(sqlGrades)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int sid = rs.getInt("student_id");
                        int aid = rs.getInt("assignment_id");
                        Map<String, Object> cell = new LinkedHashMap<>();
                        double grade = rs.getDouble("grade");
                        if (!rs.wasNull()) cell.put("grade", grade);
                        cell.put("status", rs.getString("status"));
                        cell.put("isLate", rs.getBoolean("is_late"));
                        cell.put("submissionId", rs.getInt("submission_id"));
                        if (matrix.containsKey(sid)) {
                            matrix.get(sid).put(aid, cell);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compute per-assignment class average + per-student average
        Map<Integer, Double> assignmentAvgs = new LinkedHashMap<>();
        for (Map<String, Object> a : assignments) {
            int aid = (int) a.get("id");
            double sum = 0; int count = 0;
            for (Map.Entry<Integer, Map<Integer, Map<String, Object>>> entry : matrix.entrySet()) {
                Map<String, Object> cell = entry.getValue().get(aid);
                if (cell != null && cell.containsKey("grade")) {
                    sum += (double) cell.get("grade");
                    count++;
                }
            }
            assignmentAvgs.put(aid, count > 0 ? Math.round(sum / count * 10.0) / 10.0 : -1.0);
        }

        Map<Integer, Double> studentAvgs = new LinkedHashMap<>();
        for (Map<String, Object> s : students) {
            int sid = (int) s.get("id");
            double sum = 0; int count = 0;
            Map<Integer, Map<String, Object>> row = matrix.get(sid);
            if (row != null) {
                for (Map<String, Object> cell : row.values()) {
                    if (cell.containsKey("grade")) {
                        sum += (double) cell.get("grade");
                        count++;
                    }
                }
            }
            studentAvgs.put(sid, count > 0 ? Math.round(sum / count * 10.0) / 10.0 : -1.0);
        }

        request.setAttribute("selectedCourseId", courseId);
        request.setAttribute("gbAssignments", assignments);
        request.setAttribute("gbStudents", students);
        request.setAttribute("gbMatrix", matrix);
        request.setAttribute("assignmentAvgs", assignmentAvgs);
        request.setAttribute("studentAvgs", studentAvgs);

        request.getRequestDispatcher("/instructor/gradebook.jsp").forward(request, response);
    }
}
