package dao;

import config.DBContext;
import model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Enrollment operations.
 */
public class EnrollmentDAO {

    /**
     * Get all enrollments with student and course names.
     */
    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name AS student_name, c.course_name "
                   + "FROM Enrollments e "
                   + "JOIN Users u ON e.student_id = u.user_id "
                   + "JOIN Courses c ON e.course_id = c.course_id "
                   + "ORDER BY e.enrolled_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEnrollment(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get enrollments for a course.
     */
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name AS student_name, c.course_name "
                   + "FROM Enrollments e "
                   + "JOIN Users u ON e.student_id = u.user_id "
                   + "JOIN Courses c ON e.course_id = c.course_id "
                   + "WHERE e.course_id = ? ORDER BY u.full_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEnrollment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Enroll a student in a course.
     */
    public boolean enroll(int studentId, int courseId) {
        String sql = "INSERT INTO Enrollments (student_id, course_id) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Unenroll a student from a course.
     */
    public boolean unenroll(int enrollmentId) {
        String sql = "DELETE FROM Enrollments WHERE enrollment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Student self-enrollment with capacity check.
     * Returns: 1=success, 0=already enrolled, -1=class full, -2=error
     */
    public int selfEnroll(int studentId, int courseId) {
        try (Connection conn = DBContext.getConnection()) {
            // Check if already enrolled
            String checkSql = "SELECT COUNT(*) FROM Enrollments WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return 0; // already enrolled
                }
            }
            // Check capacity
            String capSql = "SELECT c.max_students, (SELECT COUNT(*) FROM Enrollments e WHERE e.course_id = c.course_id) AS current_count "
                          + "FROM Courses c WHERE c.course_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(capSql)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int max = rs.getInt("max_students");
                        int current = rs.getInt("current_count");
                        if (max > 0 && current >= max) return -1; // class full
                    }
                }
            }
            // Enroll
            String insertSql = "INSERT INTO Enrollments (student_id, course_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                return ps.executeUpdate() > 0 ? 1 : -2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * Check if a student is enrolled in a course.
     */
    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Enroll ALL students who are not yet enrolled in a course.
     * Returns the number of students newly enrolled.
     */
    public int enrollAllStudents(int courseId) {
        String sql = "INSERT INTO Enrollments (student_id, course_id) "
                   + "SELECT u.user_id, ? FROM Users u "
                   + "WHERE u.role = 'student' AND u.is_active = 1 "
                   + "AND u.user_id NOT IN (SELECT student_id FROM Enrollments WHERE course_id = ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, courseId);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count enrollments for a student.
     */
    public int countByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE student_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Enrollment> searchEnrollments(String keyword, String courseIdStr, int offset, int limit) {
        List<Enrollment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, u.full_name AS student_name, c.course_name " +
            "FROM Enrollments e " +
            "JOIN Users u ON e.student_id = u.user_id " +
            "JOIN Courses c ON e.course_id = c.course_id " +
            "WHERE 1=1 ");
        
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasCourse = (courseIdStr != null && !courseIdStr.trim().isEmpty());
        
        if (hasKeyword) sql.append(" AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
        if (hasCourse) sql.append(" AND e.course_id = ? ");
        
        sql.append(" ORDER BY e.enrolled_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasKeyword) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(index++, k);
                ps.setString(index++, k);
                ps.setString(index++, k);
            }
            if (hasCourse) ps.setInt(index++, Integer.parseInt(courseIdStr));
            
            ps.setInt(index++, offset);
            ps.setInt(index++, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToEnrollment(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countSearchEnrollments(String keyword, String courseIdStr) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Enrollments e " +
            "JOIN Users u ON e.student_id = u.user_id " +
            "WHERE 1=1 ");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasCourse = (courseIdStr != null && !courseIdStr.trim().isEmpty());
        
        if (hasKeyword) sql.append(" AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
        if (hasCourse) sql.append(" AND e.course_id = ? ");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasKeyword) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(index++, k);
                ps.setString(index++, k);
                ps.setString(index++, k);
            }
            if (hasCourse) ps.setInt(index++, Integer.parseInt(courseIdStr));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setEnrolledAt(rs.getTimestamp("enrolled_at"));
        e.setStudentName(rs.getString("student_name"));
        e.setCourseName(rs.getString("course_name"));
        return e;
    }
}
