package dao;

import config.DBContext;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course operations.
 * Updated: JOINs Semesters + Subjects. Supports student count and open classes.
 */
public class CourseDAO {

    private static final String BASE_SELECT =
        "SELECT c.*, u.full_name AS instructor_name, "
        + "s.semester_name, sub.subject_code, sub.subject_name AS subject_full_name, "
        + "(SELECT COUNT(*) FROM Enrollments e WHERE e.course_id = c.course_id) AS student_count "
        + "FROM Courses c "
        + "JOIN Users u ON c.instructor_id = u.user_id "
        + "LEFT JOIN Semesters s ON c.semester_id = s.semester_id "
        + "LEFT JOIN Subjects sub ON c.subject_id = sub.subject_id ";

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY c.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { list.add(map(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Course> getCoursesByInstructor(int instructorId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.instructor_id = ? ORDER BY c.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { list.add(map(rs)); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Course> getCoursesByStudent(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT
            + "JOIN Enrollments en ON c.course_id = en.course_id "
            + "WHERE en.student_id = ? AND c.is_active = 1 ORDER BY c.course_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { list.add(map(rs)); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Get active classes the student is NOT enrolled in (for self-enrollment).
     */
    public List<Course> getOpenClassesForEnrollment(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = BASE_SELECT
            + "WHERE c.is_active = 1 "
            + "AND c.course_id NOT IN (SELECT course_id FROM Enrollments WHERE student_id = ?) "
            + "ORDER BY s.semester_name, c.course_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { list.add(map(rs)); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Course getCourseById(int courseId) {
        String sql = BASE_SELECT + "WHERE c.course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean createCourse(Course course) {
        String sql = "INSERT INTO Courses (course_code, course_name, description, instructor_id, semester_id, subject_id, max_students) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setInt(4, course.getInstructorId());
            if (course.getSemesterId() > 0) ps.setInt(5, course.getSemesterId()); else ps.setNull(5, Types.INTEGER);
            if (course.getSubjectId() > 0) ps.setInt(6, course.getSubjectId()); else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, course.getMaxStudents() > 0 ? course.getMaxStudents() : 40);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE Courses SET course_code=?, course_name=?, description=?, instructor_id=?, "
                   + "semester_id=?, subject_id=?, max_students=? WHERE course_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDescription());
            ps.setInt(4, course.getInstructorId());
            if (course.getSemesterId() > 0) ps.setInt(5, course.getSemesterId()); else ps.setNull(5, Types.INTEGER);
            if (course.getSubjectId() > 0) ps.setInt(6, course.getSubjectId()); else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, course.getMaxStudents() > 0 ? course.getMaxStudents() : 40);
            ps.setInt(8, course.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean toggleActive(int courseId) {
        String sql = "UPDATE Courses SET is_active = CASE WHEN is_active = 1 THEN 0 ELSE 1 END WHERE course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public int countCourses() {
        String sql = "SELECT COUNT(*) FROM Courses";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int countStudentsInCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Delete a course. Throws exception if tied to enrollments, assignments.
     */
    public void deleteCourse(int id) throws Exception {
        String sql = "DELETE FROM Courses WHERE course_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDescription(rs.getString("description"));
        c.setInstructorId(rs.getInt("instructor_id"));
        c.setInstructorName(rs.getString("instructor_name"));
        c.setIsActive(rs.getBoolean("is_active"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setStudentCount(rs.getInt("student_count"));
        // New nullable fields
        c.setSemesterId(rs.getInt("semester_id"));
        c.setSubjectId(rs.getInt("subject_id"));
        c.setMaxStudents(rs.getInt("max_students"));
        c.setSemesterName(rs.getString("semester_name"));
        String subjectCode = rs.getString("subject_code");
        String subjectFullName = rs.getString("subject_full_name");
        c.setSubjectName(subjectCode != null ? subjectCode + " - " + subjectFullName : null);
        return c;
    }

    /**
     * Feature 1: Get per-course statistics for instructor dashboard.
     * Sorted by pending grading count descending.
     */
    public List<model.CourseStats> getCourseStatsByInstructor(int instructorId) {
        List<model.CourseStats> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, "
                   + "(SELECT COUNT(*) FROM Assignments a WHERE a.course_id = c.course_id) AS total_assignments, "
                   + "(SELECT COUNT(*) FROM Submissions sub "
                   + "  JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
                   + "  WHERE a2.course_id = c.course_id AND sub.grade IS NULL) AS pending_grading, "
                   + "(SELECT COUNT(*) FROM Enrollments e WHERE e.course_id = c.course_id) AS total_students, "
                   + "(SELECT MIN(a3.deadline) FROM Assignments a3 "
                   + "  WHERE a3.course_id = c.course_id AND a3.deadline > GETDATE()) AS nearest_deadline "
                   + "FROM Courses c WHERE c.instructor_id = ? AND c.is_active = 1 "
                   + "ORDER BY pending_grading DESC, c.course_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.CourseStats cs = new model.CourseStats();
                    cs.setCourseId(rs.getInt("course_id"));
                    cs.setCourseCode(rs.getString("course_code"));
                    cs.setCourseName(rs.getString("course_name"));
                    cs.setTotalAssignments(rs.getInt("total_assignments"));
                    cs.setPendingGrading(rs.getInt("pending_grading"));
                    cs.setTotalStudents(rs.getInt("total_students"));
                    cs.setNearestDeadline(rs.getTimestamp("nearest_deadline"));
                    list.add(cs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
