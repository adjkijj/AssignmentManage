package dao;

import config.DBContext;
import model.Assignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Assignment operations.
 */
public class AssignmentDAO {

    /**
     * Get assignments by course ID.
     */
    public List<Assignment> getAssignmentsByCourse(int courseId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "WHERE a.course_id = ? ORDER BY a.deadline DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAssignment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get assignments by instructor ID (all courses).
     */
    public List<Assignment> getAssignmentsByInstructor(int instructorId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "WHERE a.instructor_id = ? ORDER BY a.deadline DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAssignment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get assignments for a student (from enrolled courses).
     */
    public List<Assignment> getAssignmentsByStudent(int studentId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "JOIN Enrollments e ON c.course_id = e.course_id "
                   + "WHERE e.student_id = ? ORDER BY a.deadline DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAssignment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get assignment by ID.
     */
    public Assignment getAssignmentById(int assignmentId) {
        String sql = "SELECT a.*, c.course_name FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "WHERE a.assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAssignment(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new assignment.
     */
    public boolean createAssignment(Assignment assignment) {
        String sql = "INSERT INTO Assignments (course_id, instructor_id, title, description, deadline, soft_deadline, hard_deadline, attachment_path) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignment.getCourseId());
            ps.setInt(2, assignment.getInstructorId());
            ps.setString(3, assignment.getTitle());
            ps.setString(4, assignment.getDescription());
            ps.setTimestamp(5, new Timestamp(assignment.getDeadline().getTime()));
            if (assignment.getSoftDeadline() != null) {
                ps.setTimestamp(6, new Timestamp(assignment.getSoftDeadline().getTime()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            if (assignment.getHardDeadline() != null) {
                ps.setTimestamp(7, new Timestamp(assignment.getHardDeadline().getTime()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }
            ps.setString(8, assignment.getAttachmentPath());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an assignment.
     */
    public boolean updateAssignment(Assignment assignment) {
        String sql = "UPDATE Assignments SET title = ?, description = ?, deadline = ?, soft_deadline = ?, hard_deadline = ?, attachment_path = ?, course_id = ? "
                   + "WHERE assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assignment.getTitle());
            ps.setString(2, assignment.getDescription());
            ps.setTimestamp(3, new Timestamp(assignment.getDeadline().getTime()));
            if (assignment.getSoftDeadline() != null) {
                ps.setTimestamp(4, new Timestamp(assignment.getSoftDeadline().getTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            if (assignment.getHardDeadline() != null) {
                ps.setTimestamp(5, new Timestamp(assignment.getHardDeadline().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, assignment.getAttachmentPath());
            ps.setInt(7, assignment.getCourseId());
            ps.setInt(8, assignment.getAssignmentId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete an assignment.
     */
    public boolean deleteAssignment(int assignmentId) {
        // Delete related records first
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete comments
                PreparedStatement ps1 = conn.prepareStatement("DELETE FROM Comments WHERE assignment_id = ?");
                ps1.setInt(1, assignmentId);
                ps1.executeUpdate();
                ps1.close();

                // Delete submissions
                PreparedStatement ps2 = conn.prepareStatement("DELETE FROM Submissions WHERE assignment_id = ?");
                ps2.setInt(1, assignmentId);
                ps2.executeUpdate();
                ps2.close();

                // Delete assignment
                PreparedStatement ps3 = conn.prepareStatement("DELETE FROM Assignments WHERE assignment_id = ?");
                ps3.setInt(1, assignmentId);
                int result = ps3.executeUpdate();
                ps3.close();

                conn.commit();
                return result > 0;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Count assignments by instructor.
     */
    public int countByInstructor(int instructorId) {
        String sql = "SELECT COUNT(*) FROM Assignments WHERE instructor_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Count assignments for a student.
     */
    public int countByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM Assignments a "
                   + "JOIN Enrollments e ON a.course_id = e.course_id "
                   + "WHERE e.student_id = ?";
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

    /**
     * Get upcoming assignments for a student (deadline in the future).
     */
    /**
     * Get upcoming assignments for a student (deadline in the future).
     */
    public List<Assignment> getUpcomingByStudent(int studentId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "JOIN Enrollments e ON c.course_id = e.course_id "
                   + "WHERE e.student_id = ? AND a.deadline >= GETDATE() "
                   + "ORDER BY a.deadline ASC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAssignment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================================================================
    //  Feature 1+6: Assignments with submission status (badges + filter)
    // ================================================================

    /**
     * Get assignments for a student with submission status info (LEFT JOIN).
     * Returns assignments sorted: near deadline first, graded last.
     */
    public List<Assignment> getAssignmentsWithStatusByStudent(int studentId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name, c.course_code, "
                   + "sub.status AS sub_status, sub.grade AS sub_grade, sub.is_late AS sub_is_late "
                   + "FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "JOIN Enrollments e ON c.course_id = e.course_id "
                   + "LEFT JOIN (SELECT assignment_id, student_id, status, grade, is_late, "
                   + "           ROW_NUMBER() OVER (PARTITION BY assignment_id, student_id ORDER BY version DESC) as rn "
                   + "           FROM Submissions) sub "
                   + "  ON sub.assignment_id = a.assignment_id AND sub.student_id = e.student_id AND sub.rn = 1 "
                   + "WHERE e.student_id = ? "
                   + "ORDER BY "
                   + "  CASE WHEN sub.grade IS NOT NULL THEN 3 "       // graded → bottom
                   + "       WHEN a.deadline < GETDATE() AND sub.status IS NULL THEN 2 "  // overdue unsubmitted
                   + "       WHEN sub.status IS NOT NULL THEN 1 "      // submitted
                   + "       ELSE 0 END, "                             // not submitted yet → top
                   + "  a.deadline ASC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = mapResultSetToAssignment(rs);
                    // Set submission-related transient fields
                    String subStatus = rs.getString("sub_status");
                    double grade = rs.getDouble("sub_grade");
                    boolean hasGrade = !rs.wasNull();
                    boolean isLate = rs.getBoolean("sub_is_late");

                    if (hasGrade) {
                        a.setSubmissionStatus("graded");
                        a.setGradeValue(grade);
                    } else if (isLate && subStatus != null) {
                        a.setSubmissionStatus("late");
                    } else if (subStatus != null) {
                        a.setSubmissionStatus("submitted");
                    }
                    // else null = not submitted
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Feature 6: Filtered assignment list for student.
     */
    public List<Assignment> getAssignmentsWithFilter(int studentId, Integer courseId, String status, String keyword) {
        List<Assignment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.*, c.course_name, c.course_code, ");
        sql.append("sub.status AS sub_status, sub.grade AS sub_grade, sub.is_late AS sub_is_late ");
        sql.append("FROM Assignments a ");
        sql.append("JOIN Courses c ON a.course_id = c.course_id ");
        sql.append("JOIN Enrollments e ON c.course_id = e.course_id ");
        sql.append("LEFT JOIN (SELECT assignment_id, student_id, status, grade, is_late, ");
        sql.append("           ROW_NUMBER() OVER (PARTITION BY assignment_id, student_id ORDER BY version DESC) as rn ");
        sql.append("           FROM Submissions) sub ");
        sql.append("  ON sub.assignment_id = a.assignment_id AND sub.student_id = e.student_id AND sub.rn = 1 ");
        sql.append("WHERE e.student_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (courseId != null) {
            sql.append("AND a.course_id = ? ");
            params.add(courseId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND a.title LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        // Status filter applied after data retrieval for complex conditions
        // but handle simple ones in SQL
        if ("graded".equals(status)) {
            sql.append("AND sub.grade IS NOT NULL ");
        } else if ("submitted".equals(status)) {
            sql.append("AND sub.status IS NOT NULL AND sub.grade IS NULL ");
        } else if ("unsubmitted".equals(status)) {
            sql.append("AND sub.status IS NULL ");
        } else if ("near_deadline".equals(status)) {
            sql.append("AND sub.status IS NULL AND a.deadline >= GETDATE() AND a.deadline <= DATEADD(HOUR, 48, GETDATE()) ");
        }

        sql.append("ORDER BY a.deadline ASC");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = mapResultSetToAssignment(rs);
                    String subStatus = rs.getString("sub_status");
                    double grade = rs.getDouble("sub_grade");
                    boolean hasGrade = !rs.wasNull();
                    boolean isLate = rs.getBoolean("sub_is_late");
                    if (hasGrade) { a.setSubmissionStatus("graded"); a.setGradeValue(grade); }
                    else if (isLate && subStatus != null) { a.setSubmissionStatus("late"); }
                    else if (subStatus != null) { a.setSubmissionStatus("submitted"); }
                    list.add(a);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ================================================================
    //  Feature 2: Progress by course (for dashboard progress bars)
    // ================================================================

    /**
     * Returns a list of Object[]{courseId, courseName, courseCode, totalAssignments, submittedCount}
     */
    public List<Object[]> getProgressByStudent(int studentId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_name, c.course_code, "
                   + " COUNT(DISTINCT a.assignment_id) AS total_assignments, "
                   + " COUNT(DISTINCT CASE WHEN sub.submission_id IS NOT NULL THEN a.assignment_id END) AS submitted_count "
                   + "FROM Courses c "
                   + "JOIN Enrollments e ON c.course_id = e.course_id "
                   + "LEFT JOIN Assignments a ON a.course_id = c.course_id "
                   + "LEFT JOIN Submissions sub ON sub.assignment_id = a.assignment_id AND sub.student_id = e.student_id "
                   + "WHERE e.student_id = ? "
                   + "GROUP BY c.course_id, c.course_name, c.course_code "
                   + "ORDER BY c.course_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("course_code"),
                        rs.getInt("total_assignments"),
                        rs.getInt("submitted_count")
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ================================================================
    //  Feature 4: Todo items (urgent assignments for dashboard)
    // ================================================================

    /**
     * Returns assignments needing action, sorted by urgency.
     * Each Object[]: {assignmentId, title, courseName, deadline, type(1-4), message}
     * Type 1 = urgent (24h), Type 2 = soon (72h), Type 3 = recently graded, Type 4 = unsubmitted
     */
    public List<Object[]> getTodoItemsByStudent(int studentId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT a.assignment_id, a.title, c.course_name, a.deadline, "
                   + "  sub.status AS sub_status, sub.grade AS sub_grade, sub.graded_at AS sub_graded_at, "
                   + "  CASE "
                   + "    WHEN sub.status IS NULL AND a.deadline >= GETDATE() AND a.deadline < DATEADD(HOUR, 24, GETDATE()) THEN 1 "
                   + "    WHEN sub.status IS NULL AND a.deadline >= GETDATE() AND a.deadline < DATEADD(HOUR, 72, GETDATE()) THEN 2 "
                   + "    WHEN sub.grade IS NOT NULL AND sub.graded_at >= DATEADD(DAY, -7, GETDATE()) THEN 3 "
                   + "    WHEN sub.status IS NULL AND a.deadline >= GETDATE() THEN 4 "
                   + "    ELSE 99 END AS todo_type "
                   + "FROM Assignments a "
                   + "JOIN Courses c ON a.course_id = c.course_id "
                   + "JOIN Enrollments e ON c.course_id = e.course_id "
                   + "LEFT JOIN (SELECT assignment_id, student_id, status, grade, graded_at, "
                   + "           ROW_NUMBER() OVER (PARTITION BY assignment_id, student_id ORDER BY version DESC) as rn "
                   + "           FROM Submissions) sub "
                   + "  ON sub.assignment_id = a.assignment_id AND sub.student_id = e.student_id AND sub.rn = 1 "
                   + "WHERE e.student_id = ? "
                   + "HAVING CASE "
                   + "  WHEN sub.status IS NULL AND a.deadline >= GETDATE() AND a.deadline < DATEADD(HOUR, 24, GETDATE()) THEN 1 "
                   + "  WHEN sub.status IS NULL AND a.deadline >= GETDATE() AND a.deadline < DATEADD(HOUR, 72, GETDATE()) THEN 2 "
                   + "  WHEN sub.grade IS NOT NULL AND sub.graded_at >= DATEADD(DAY, -7, GETDATE()) THEN 3 "
                   + "  WHEN sub.status IS NULL AND a.deadline >= GETDATE() THEN 4 "
                   + "  ELSE 99 END < 99 "
                   + "ORDER BY todo_type ASC, a.deadline ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next() && count < 10) {
                    int type = rs.getInt("todo_type");
                    String msg;
                    switch (type) {
                        case 1: msg = "Deadline trong 24 giờ!"; break;
                        case 2: msg = "Deadline trong 3 ngày"; break;
                        case 3: msg = "Có điểm mới"; break;
                        default: msg = "Chưa nộp bài"; break;
                    }
                    list.add(new Object[]{
                        rs.getInt("assignment_id"),
                        rs.getString("title"),
                        rs.getString("course_name"),
                        rs.getTimestamp("deadline"),
                        type,
                        msg
                    });
                    count++;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private Assignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setAssignmentId(rs.getInt("assignment_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setInstructorId(rs.getInt("instructor_id"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setDeadline(rs.getTimestamp("deadline"));
        a.setSoftDeadline(rs.getTimestamp("soft_deadline"));
        a.setHardDeadline(rs.getTimestamp("hard_deadline"));
        a.setAttachmentPath(rs.getString("attachment_path"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setCourseName(rs.getString("course_name"));
        try { a.setCourseCode(rs.getString("course_code")); } catch (SQLException ignored) {}
        return a;
    }

    /**
     * Feature 2: Get assignments by instructor with filters and pending count.
     */
    public List<Assignment> getAssignmentsByInstructorWithFilter(int instructorId,
            Integer courseId, String gradingStatus, String keyword) {
        List<Assignment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.*, c.course_name, c.course_code, ");
        sql.append("(SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id AND sub.grade IS NULL) AS pending_count ");
        sql.append("FROM Assignments a JOIN Courses c ON a.course_id = c.course_id ");
        sql.append("WHERE a.instructor_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(instructorId);

        if (courseId != null) {
            sql.append("AND a.course_id = ? ");
            params.add(courseId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND a.title LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if ("pending".equals(gradingStatus)) {
            sql.append("AND (SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id AND sub.grade IS NULL) > 0 ");
        } else if ("all_graded".equals(gradingStatus)) {
            sql.append("AND (SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id AND sub.grade IS NULL) = 0 ");
        } else if ("near_deadline".equals(gradingStatus)) {
            sql.append("AND a.deadline > GETDATE() AND a.deadline <= DATEADD(day, 7, GETDATE()) ");
        } else if ("past_deadline".equals(gradingStatus)) {
            sql.append("AND a.deadline < GETDATE() ");
        }

        sql.append("ORDER BY a.deadline DESC");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = mapResultSetToAssignment(rs);
                    a.setPendingCount(rs.getInt("pending_count"));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Feature 11: Duplicate an assignment to a different course.
     * Uses INSERT...SELECT to copy metadata, updating course/deadline/instructor.
     * Returns the new assignment_id, or -1 on failure.
     */
    public int duplicateAssignment(int originalId, int targetCourseId, int instructorId,
                                   java.util.Date newDeadline, java.util.Date newSoftDeadline, java.util.Date newHardDeadline) {
        String sql = "INSERT INTO Assignments (course_id, instructor_id, title, description, attachment_path, "
                   + "deadline, soft_deadline, hard_deadline, created_at) "
                   + "SELECT ?, ?, title, description, attachment_path, ?, ?, ?, GETDATE() "
                   + "FROM Assignments WHERE assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, targetCourseId);
            ps.setInt(2, instructorId);
            ps.setTimestamp(3, new java.sql.Timestamp(newDeadline.getTime()));
            if (newSoftDeadline != null) ps.setTimestamp(4, new java.sql.Timestamp(newSoftDeadline.getTime()));
            else ps.setNull(4, java.sql.Types.TIMESTAMP);
            if (newHardDeadline != null) ps.setTimestamp(5, new java.sql.Timestamp(newHardDeadline.getTime()));
            else ps.setNull(5, java.sql.Types.TIMESTAMP);
            ps.setInt(6, originalId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
