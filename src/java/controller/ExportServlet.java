package controller;

import config.DBContext;
import dao.AssignmentDAO;
import dao.AuditLogDAO;
import dao.CourseDAO;
import model.Assignment;
import model.Course;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Feature 9: Export grades to CSV.
 * Access: /export?type=grades&courseId=X&format=csv
 */
public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"instructor".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String type = request.getParameter("type");
        if (!"grades".equals(type)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(request.getParameter("courseId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/gradebook");
            return;
        }

        // Ownership check
        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getCoursesByInstructor(currentUser.getUserId());
        Course targetCourse = null;
        for (Course c : courses) {
            if (c.getCourseId() == courseId) { targetCourse = c; break; }
        }
        if (targetCourse == null) {
            response.sendRedirect(request.getContextPath() + "/gradebook");
            return;
        }

        // Gather data
        List<Map<String, Object>> assignments = new ArrayList<>();
        List<Map<String, Object>> students = new ArrayList<>();
        Map<Integer, Map<Integer, Double>> gradeMap = new LinkedHashMap<>(); // studentId -> assignmentId -> grade (null = not graded or no sub)
        Map<Integer, Map<Integer, String>> statusMap = new LinkedHashMap<>(); // studentId -> assignmentId -> "submitted"/"late"/"graded"/null

        String sqlA = "SELECT assignment_id, title FROM Assignments WHERE course_id = ? ORDER BY deadline ASC";
        String sqlS = "SELECT u.user_id, u.full_name, u.username, u.email FROM Users u "
                     + "JOIN Enrollments e ON u.user_id = e.student_id WHERE e.course_id = ? ORDER BY u.full_name";
        String sqlG = "SELECT sub.student_id, sub.assignment_id, sub.grade, sub.status "
                    + "FROM Submissions sub JOIN Assignments a ON sub.assignment_id = a.assignment_id "
                    + "WHERE a.course_id = ? AND sub.version = "
                    + "(SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)";

        try (Connection conn = DBContext.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlA)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> a = new LinkedHashMap<>();
                        a.put("id", rs.getInt("assignment_id"));
                        a.put("title", rs.getString("title"));
                        assignments.add(a);
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlS)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> s = new LinkedHashMap<>();
                        int sid = rs.getInt("user_id");
                        s.put("id", sid);
                        s.put("fullName", rs.getString("full_name"));
                        s.put("username", rs.getString("username"));
                        s.put("email", rs.getString("email"));
                        students.add(s);
                        gradeMap.put(sid, new LinkedHashMap<>());
                        statusMap.put(sid, new LinkedHashMap<>());
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlG)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int sid = rs.getInt("student_id");
                        int aid = rs.getInt("assignment_id");
                        double grade = rs.getDouble("grade");
                        if (gradeMap.containsKey(sid)) {
                            gradeMap.get(sid).put(aid, rs.wasNull() ? null : grade);
                            statusMap.get(sid).put(aid, rs.getString("status"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Audit log
        new AuditLogDAO().logAction(currentUser.getUserId(), "EXPORT_GRADES", courseId);

        // Export CSV
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String fileName = "Grades_" + targetCourse.getCourseCode() + "_" + dateStr + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (PrintWriter out = response.getWriter()) {
            // Write BOM for UTF-8 Excel support
            out.write('\ufeff');

            // Header row
            out.print("\"Student Name\",\"Username\",\"Email\"");
            for (Map<String, Object> a : assignments) {
                out.print(",\"" + a.get("title") + "\"");
            }
            out.println();

            // Data rows
            for (Map<String, Object> s : students) {
                out.print("\"" + s.get("fullName") + "\",\"" + s.get("username") + "\",\"" + s.get("email") + "\"");
                int sid = (int) s.get("id");
                for (Map<String, Object> a : assignments) {
                    int aid = (int) a.get("id");
                    Double grade = gradeMap.get(sid).get(aid);
                    String status = statusMap.get(sid).get(aid);
                    if (grade != null) {
                        out.print("," + grade);
                    } else if (status != null) {
                        out.print(",Ungraded");
                    } else {
                        out.print(",NoSubmission");
                    }
                }
                out.println();
            }
        }
    }
}
