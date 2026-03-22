package dao;

import config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public int getCount(String sql) {
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalUsers() {
        return getCount("SELECT COUNT(*) FROM Users");
    }

    public int countTotalCourses() {
        return getCount("SELECT COUNT(*) FROM Courses");
    }

    public int countTotalSubmissions() {
        return getCount("SELECT COUNT(*) FROM Submissions");
    }

    public int countSubmissionsToday() {
        return getCount("SELECT COUNT(*) FROM Submissions WHERE CAST(submitted_at AS DATE) = CAST(GETDATE() AS DATE)");
    }

    public int countActiveCoursesThisSemester() {
        return getCount("SELECT COUNT(*) FROM Courses WHERE is_active = 1");
    }

    public List<Map<String, Object>> getRecentLogins(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " username, action, ip_address, created_at "
                + "FROM AuditLog WHERE action = 'LOGIN' ORDER BY created_at DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("username", rs.getString("username"));
                map.put("time", rs.getTimestamp("created_at"));
                map.put("ip", rs.getString("ip_address"));
                list.add(map);
            }
        } catch (Exception e) {
            // AuditLog table might not exist or query failed - return empty list
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getRecentSubmissions(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " u.username, u.full_name, c.course_code, a.title as assignment_title, s.submitted_at "
                + "FROM Submissions s "
                + "JOIN Users u ON s.student_id = u.user_id "
                + "JOIN Assignments a ON s.assignment_id = a.assignment_id "
                + "JOIN Courses c ON a.course_id = c.course_id "
                + "ORDER BY s.submitted_at DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentName", rs.getString("full_name"));
                map.put("username", rs.getString("username"));
                map.put("courseCode", rs.getString("course_code"));
                map.put("assignmentTitle", rs.getString("assignment_title"));
                map.put("time", rs.getTimestamp("submitted_at"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getSubmissionTrend(int lastNDays) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT CAST(submitted_at AS DATE) as sub_date, COUNT(*) as cnt "
                + "FROM Submissions "
                + "WHERE submitted_at >= DATEADD(day, -?, GETDATE()) "
                + "GROUP BY CAST(submitted_at AS DATE) "
                + "ORDER BY sub_date ASC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lastNDays);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", rs.getDate("sub_date").toString());
                    map.put("count", rs.getInt("cnt"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getTopCoursesBySubmission(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " c.course_code, COUNT(s.submission_id) as sub_count "
                + "FROM Courses c "
                + "LEFT JOIN Assignments a ON c.course_id = a.course_id "
                + "LEFT JOIN Submissions s ON a.assignment_id = s.assignment_id "
                + "GROUP BY c.course_id, c.course_code "
                + "ORDER BY sub_count DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("courseCode", rs.getString("course_code"));
                map.put("count", rs.getInt("sub_count"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
