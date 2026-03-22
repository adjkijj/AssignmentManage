package dao;

import config.DBContext;
import model.Assignment;
import model.CourseDetail;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailDAO {

    public List<User> getEnrolledStudents(int courseId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u JOIN Enrollments e ON u.user_id = e.student_id WHERE e.course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    u.setIsActive(rs.getBoolean("is_active"));
                    students.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<CourseDetail.AssignmentStat> getAssignmentStats(int courseId) {
        List<CourseDetail.AssignmentStat> stats = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "(SELECT COUNT(*) FROM Submissions s WHERE s.assignment_id = a.assignment_id) as sub_count " +
                     "FROM Assignments a WHERE a.course_id = ? ORDER BY a.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = new Assignment();
                    a.setAssignmentId(rs.getInt("assignment_id"));
                    a.setCourseId(rs.getInt("course_id"));
                    a.setTitle(rs.getString("title"));
                    a.setDeadline(rs.getTimestamp("deadline"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    int count = rs.getInt("sub_count");
                    stats.add(new CourseDetail.AssignmentStat(a, count));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
