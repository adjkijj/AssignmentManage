package dao;

import config.DBContext;
import model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Submission operations.
 * Extended: version control, status tracking, late flag, group support.
 */
public class SubmissionDAO {

    /**
     * Get submissions for an assignment (for instructor grading).
     */
    public List<Submission> getSubmissionsByAssignment(int assignmentId) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name AS student_name, a.title AS assignment_title "
                   + "FROM Submissions s "
                   + "JOIN Users u ON s.student_id = u.user_id "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE s.assignment_id = ? ORDER BY s.submitted_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSubmission(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get submissions by student (for viewing grades).
     */
    public List<Submission> getSubmissionsByStudent(int studentId) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name AS student_name, a.title AS assignment_title "
                   + "FROM Submissions s "
                   + "JOIN Users u ON s.student_id = u.user_id "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE s.student_id = ? ORDER BY s.submitted_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSubmission(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get a specific submission by student and assignment (latest version).
     */
    public Submission getSubmission(int assignmentId, int studentId) {
        String sql = "SELECT TOP 1 s.*, u.full_name AS student_name, a.title AS assignment_title "
                   + "FROM Submissions s "
                   + "JOIN Users u ON s.student_id = u.user_id "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE s.assignment_id = ? AND s.student_id = ? "
                   + "ORDER BY s.version DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubmission(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get submission by ID.
     */
    public Submission getSubmissionById(int submissionId) {
        String sql = "SELECT s.*, u.full_name AS student_name, a.title AS assignment_title "
                   + "FROM Submissions s "
                   + "JOIN Users u ON s.student_id = u.user_id "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE s.submission_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubmission(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the latest version number for a student + assignment combo.
     */
    public int getLatestVersion(int studentId, int assignmentId) {
        String sql = "SELECT ISNULL(MAX(version), 0) FROM Submissions WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get all submission versions for a student + assignment (history view).
     */
    public List<Submission> getSubmissionHistory(int studentId, int assignmentId) {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name AS student_name, a.title AS assignment_title "
                   + "FROM Submissions s "
                   + "JOIN Users u ON s.student_id = u.user_id "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE s.student_id = ? AND s.assignment_id = ? ORDER BY s.version DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSubmission(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Submit an assignment (create new version).
     */
    public boolean submitAssignment(Submission submission) {
        String sql = "INSERT INTO Submissions (assignment_id, student_id, file_path, version, status, is_late, group_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submission.getAssignmentId());
            ps.setInt(2, submission.getStudentId());
            ps.setString(3, submission.getFilePath());
            ps.setInt(4, submission.getVersion());
            ps.setString(5, submission.getStatus());
            ps.setBoolean(6, submission.isIsLate());
            if (submission.getGroupId() != null) {
                ps.setInt(7, submission.getGroupId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update submission file (re-submit).
     */
    public boolean updateSubmission(int submissionId, String filePath) {
        String sql = "UPDATE Submissions SET file_path = ?, submitted_at = GETDATE() WHERE submission_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filePath);
            ps.setInt(2, submissionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Grade a submission (also sets status to 'graded').
     */
    public boolean gradeSubmission(int submissionId, double grade, String feedback) {
        String sql = "UPDATE Submissions SET grade = ?, feedback = ?, graded_at = GETDATE(), status = 'graded' WHERE submission_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, grade);
            ps.setString(2, feedback);
            ps.setInt(3, submissionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Count submissions for a student.
     */
    public int countByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM Submissions WHERE student_id = ?";
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
     * Count graded submissions for a student.
     */
    public int countGradedByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM Submissions WHERE student_id = ? AND grade IS NOT NULL";
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
     * Count pending (ungraded) submissions for an instructor.
     */
    public int countPendingByInstructor(int instructorId) {
        String sql = "SELECT COUNT(*) FROM Submissions s "
                   + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                   + "WHERE a.instructor_id = ? AND s.grade IS NULL";
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

    private Submission mapResultSetToSubmission(ResultSet rs) throws SQLException {
        Submission s = new Submission();
        s.setSubmissionId(rs.getInt("submission_id"));
        s.setAssignmentId(rs.getInt("assignment_id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setFilePath(rs.getString("file_path"));
        s.setSubmittedAt(rs.getTimestamp("submitted_at"));
        double grade = rs.getDouble("grade");
        if (!rs.wasNull()) {
            s.setGrade(grade);
        }
        s.setFeedback(rs.getString("feedback"));
        s.setGradedAt(rs.getTimestamp("graded_at"));
        s.setVersion(rs.getInt("version"));
        s.setStatus(rs.getString("status"));
        s.setIsLate(rs.getBoolean("is_late"));
        int groupId = rs.getInt("group_id");
        if (!rs.wasNull()) {
            s.setGroupId(groupId);
        }
        s.setStudentName(rs.getString("student_name"));
        s.setAssignmentTitle(rs.getString("assignment_title"));
        return s;
    }

    /**
     * Feature 3: Get submission stats map for all assignments by an instructor.
     * Returns Map<assignmentId, SubmissionStats>.
     */
    public java.util.Map<Integer, model.SubmissionStats> getSubmissionStatsMap(int instructorId) {
        java.util.Map<Integer, model.SubmissionStats> map = new java.util.HashMap<>();
        String sql = "SELECT a.assignment_id, "
                   + "(SELECT COUNT(*) FROM Enrollments e WHERE e.course_id = a.course_id) AS total_enrolled, "
                   + "(SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id "
                   + "  AND sub.is_late = 0 AND sub.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)) AS on_time, "
                   + "(SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id "
                   + "  AND sub.is_late = 1 AND sub.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)) AS late_count, "
                   + "(SELECT COUNT(*) FROM Submissions sub WHERE sub.assignment_id = a.assignment_id "
                   + "  AND sub.grade IS NOT NULL) AS graded_count "
                   + "FROM Assignments a WHERE a.instructor_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.SubmissionStats stats = new model.SubmissionStats();
                    int assignmentId = rs.getInt("assignment_id");
                    int total = rs.getInt("total_enrolled");
                    int onTime = rs.getInt("on_time");
                    int late = rs.getInt("late_count");
                    stats.setTotalEnrolled(total);
                    stats.setSubmittedOnTime(onTime);
                    stats.setSubmittedLate(late);
                    stats.setNotSubmitted(Math.max(0, total - onTime - late));
                    stats.setGraded(rs.getInt("graded_count"));
                    map.put(assignmentId, stats);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Feature 4: Get students enrolled but not submitted for an assignment.
     */
    public List<model.User> getStudentsNotSubmitted(int assignmentId, int courseId) {
        List<model.User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email "
                   + "FROM Users u JOIN Enrollments e ON u.user_id = e.student_id "
                   + "WHERE e.course_id = ? AND u.user_id NOT IN ("
                   + "  SELECT DISTINCT s.student_id FROM Submissions s WHERE s.assignment_id = ?"
                   + ") ORDER BY u.full_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.User u = new model.User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    list.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Feature 5: Get ALL versions by a specific student + assignment.
     */
    public List<Submission> getAllVersionsByStudentAndAssignment(int studentId, int assignmentId) {
        return getSubmissionHistory(studentId, assignmentId); // Already exists, same query
    }

    /**
     * Feature 10: Get assignment statistics for histogram/stats page.
     */
    public model.AssignmentStats getAssignmentStats(int assignmentId, int courseId) {
        model.AssignmentStats stats = new model.AssignmentStats();
        String sql = "SELECT "
            + "(SELECT COUNT(*) FROM Enrollments e WHERE e.course_id = ?) AS total_enrolled, "
            + "(SELECT COUNT(*) FROM Submissions s WHERE s.assignment_id = ? "
            + "  AND s.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = s.student_id AND s2.assignment_id = s.assignment_id)) AS total_submitted, "
            + "(SELECT COUNT(*) FROM Submissions s WHERE s.assignment_id = ? AND s.grade IS NOT NULL) AS graded, "
            + "(SELECT AVG(s.grade) FROM Submissions s WHERE s.assignment_id = ? AND s.grade IS NOT NULL) AS mean_grade, "
            + "(SELECT MAX(s.grade) FROM Submissions s WHERE s.assignment_id = ? AND s.grade IS NOT NULL) AS max_grade, "
            + "(SELECT MIN(s.grade) FROM Submissions s WHERE s.assignment_id = ? AND s.grade IS NOT NULL) AS min_grade, "
            + "(SELECT STDEV(s.grade) FROM Submissions s WHERE s.assignment_id = ? AND s.grade IS NOT NULL) AS std_dev, "
            + "(SELECT COUNT(*) FROM Submissions s WHERE s.assignment_id = ? AND s.grade >= 5) AS pass_count, "
            + "(SELECT COUNT(*) FROM Submissions s WHERE s.assignment_id = ? AND s.is_late = 1 "
            + "  AND s.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = s.student_id AND s2.assignment_id = s.assignment_id)) AS late_count";
        try (java.sql.Connection conn = config.DBContext.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            for (int i = 2; i <= 9; i++) ps.setInt(i, assignmentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalEnrolled(rs.getInt("total_enrolled"));
                    stats.setTotalSubmitted(rs.getInt("total_submitted"));
                    stats.setGraded(rs.getInt("graded"));
                    stats.setMean(Math.round(rs.getDouble("mean_grade") * 10.0) / 10.0);
                    stats.setMax(rs.getDouble("max_grade"));
                    stats.setMin(rs.getDouble("min_grade"));
                    stats.setStdDev(Math.round(rs.getDouble("std_dev") * 100.0) / 100.0);
                    stats.setPassCount(rs.getInt("pass_count"));
                    stats.setLateCount(rs.getInt("late_count"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Distribution: 10 buckets
        java.util.List<int[]> dist = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) dist.add(new int[]{0});
        String distSql = "SELECT grade FROM Submissions WHERE assignment_id = ? AND grade IS NOT NULL";
        try (java.sql.Connection conn = config.DBContext.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(distSql)) {
            ps.setInt(1, assignmentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    int bucket = Math.min((int) g, 9);
                    dist.get(bucket)[0]++;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        stats.setDistribution(dist);
        return stats;
    }

    /**
     * Feature 8: Get grade distribution for an assignment.
     */
    public java.util.Map<String, Integer> getGradeDistribution(int assignmentId) {
        java.util.Map<String, Integer> dist = new java.util.LinkedHashMap<>();
        dist.put("0-4", 0);
        dist.put("4-6", 0);
        dist.put("6-8", 0);
        dist.put("8-10", 0);

        String sql = "SELECT grade FROM Submissions WHERE assignment_id = ? AND grade IS NOT NULL "
                   + "AND version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = Submissions.student_id AND s2.assignment_id = Submissions.assignment_id)";
        try (java.sql.Connection conn = config.DBContext.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    if (g < 4) dist.put("0-4", dist.get("0-4") + 1);
                    else if (g < 6) dist.put("4-6", dist.get("4-6") + 1);
                    else if (g < 8) dist.put("6-8", dist.get("6-8") + 1);
                    else dist.put("8-10", dist.get("8-10") + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dist;
    }

    /**
     * Feature 12: Get at-risk students across all courses for an instructor.
     * Criteria: 1) 2+ missed assignments past deadline, 2) >50% late submissions, 3) last 2 graded <5
     */
    public java.util.List<model.AtRiskStudent> getAtRiskStudents(int instructorId) {
        java.util.Map<String, model.AtRiskStudent> map = new java.util.LinkedHashMap<>();  // key = "studentId-courseId"

        // Criteria 1: 2+ assignments past deadline with no submission
        String sql1 = "SELECT u.user_id, u.full_name, c.course_id, c.course_name, "
            + "COUNT(*) AS missed "
            + "FROM Users u JOIN Enrollments e ON u.user_id = e.student_id "
            + "JOIN Courses c ON e.course_id = c.course_id "
            + "JOIN Assignments a ON a.course_id = c.course_id "
            + "WHERE c.instructor_id = ? AND a.deadline < GETDATE() "
            + "AND NOT EXISTS (SELECT 1 FROM Submissions s WHERE s.student_id = u.user_id AND s.assignment_id = a.assignment_id) "
            + "GROUP BY u.user_id, u.full_name, c.course_id, c.course_name HAVING COUNT(*) >= 2";

        // Criteria 2: >50% late (need at least 2 submissions)
        String sql2 = "SELECT u.user_id, u.full_name, c.course_id, c.course_name "
            + "FROM Users u JOIN Enrollments e ON u.user_id = e.student_id "
            + "JOIN Courses c ON e.course_id = c.course_id "
            + "WHERE c.instructor_id = ? "
            + "AND (SELECT COUNT(*) FROM Submissions sub JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
            + "     WHERE sub.student_id = u.user_id AND a2.course_id = c.course_id "
            + "     AND sub.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)) >= 2 "
            + "AND (SELECT COUNT(*) FROM Submissions sub JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
            + "     WHERE sub.student_id = u.user_id AND a2.course_id = c.course_id AND sub.is_late = 1 "
            + "     AND sub.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id)) * 2 > "
            + "    (SELECT COUNT(*) FROM Submissions sub JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
            + "     WHERE sub.student_id = u.user_id AND a2.course_id = c.course_id "
            + "     AND sub.version = (SELECT MAX(s2.version) FROM Submissions s2 WHERE s2.student_id = sub.student_id AND s2.assignment_id = sub.assignment_id))";

        // Criteria 3: last 2 graded assignments both <5
        String sql3 = "SELECT u.user_id, u.full_name, c.course_id, c.course_name "
            + "FROM Users u JOIN Enrollments e ON u.user_id = e.student_id "
            + "JOIN Courses c ON e.course_id = c.course_id "
            + "WHERE c.instructor_id = ? "
            + "AND (SELECT COUNT(*) FROM (SELECT TOP 2 sub.grade FROM Submissions sub "
            + "     JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
            + "     WHERE sub.student_id = u.user_id AND a2.course_id = c.course_id AND sub.grade IS NOT NULL "
            + "     ORDER BY sub.graded_at DESC) t WHERE t.grade < 5) = 2 "
            + "AND (SELECT COUNT(*) FROM Submissions sub JOIN Assignments a2 ON sub.assignment_id = a2.assignment_id "
            + "     WHERE sub.student_id = u.user_id AND a2.course_id = c.course_id AND sub.grade IS NOT NULL) >= 2";

        try (java.sql.Connection conn = config.DBContext.getConnection()) {
            // Criteria 1
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, instructorId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String key = rs.getInt("user_id") + "-" + rs.getInt("course_id");
                        model.AtRiskStudent ar = map.getOrDefault(key, new model.AtRiskStudent());
                        ar.setStudentId(rs.getInt("user_id"));
                        ar.setStudentName(rs.getString("full_name"));
                        ar.setCourseId(rs.getInt("course_id"));
                        ar.setCourseName(rs.getString("course_name"));
                        if (ar.getReasons() == null) ar.setReasons(new java.util.ArrayList<>());
                        ar.getReasons().add("Chưa nộp " + rs.getInt("missed") + " bài đã hết hạn");
                        map.put(key, ar);
                    }
                }
            }
            // Criteria 2
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, instructorId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String key = rs.getInt("user_id") + "-" + rs.getInt("course_id");
                        model.AtRiskStudent ar = map.getOrDefault(key, new model.AtRiskStudent());
                        ar.setStudentId(rs.getInt("user_id"));
                        ar.setStudentName(rs.getString("full_name"));
                        ar.setCourseId(rs.getInt("course_id"));
                        ar.setCourseName(rs.getString("course_name"));
                        if (ar.getReasons() == null) ar.setReasons(new java.util.ArrayList<>());
                        ar.getReasons().add("Tỉ lệ nộp muộn > 50%");
                        map.put(key, ar);
                    }
                }
            }
            // Criteria 3
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setInt(1, instructorId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String key = rs.getInt("user_id") + "-" + rs.getInt("course_id");
                        model.AtRiskStudent ar = map.getOrDefault(key, new model.AtRiskStudent());
                        ar.setStudentId(rs.getInt("user_id"));
                        ar.setStudentName(rs.getString("full_name"));
                        ar.setCourseId(rs.getInt("course_id"));
                        ar.setCourseName(rs.getString("course_name"));
                        if (ar.getReasons() == null) ar.setReasons(new java.util.ArrayList<>());
                        ar.getReasons().add("2 bài gần nhất đều < 5 điểm");
                        map.put(key, ar);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        return new java.util.ArrayList<>(map.values());
    }
}


