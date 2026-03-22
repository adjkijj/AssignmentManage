package dao;

import config.DBContext;
import model.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    public List<Announcement> getByInstructor(int instructorId) {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM Announcements a " +
                     "LEFT JOIN Courses c ON a.course_id = c.course_id " +
                     "WHERE a.instructor_id = ? OR (a.course_id IS NULL AND a.target_role IN ('all', 'instructor')) ORDER BY a.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Announcement a = new Announcement();
                    a.setAnnouncementId(rs.getInt("announcement_id"));
                    a.setCourseId(rs.getInt("course_id")); // will be 0 if NULL
                    a.setInstructorId(rs.getInt("instructor_id"));
                    a.setTitle(rs.getString("title"));
                    a.setContent(rs.getString("content"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setTargetRole(rs.getString("target_role"));
                    String cname = rs.getString("course_name");
                    a.setCourseName(cname == null ? "System" : cname);
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Announcement> getByStudent(int studentId) {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name, u.full_name as instructor_name FROM Announcements a " +
                     "LEFT JOIN Courses c ON a.course_id = c.course_id " +
                     "JOIN Users u ON u.user_id = a.instructor_id " +
                     "WHERE (a.course_id IS NULL AND (a.target_role IS NULL OR a.target_role = 'all')) OR a.course_id IN (SELECT course_id FROM Enrollments WHERE student_id = ?) " +
                     "ORDER BY a.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Announcement a = new Announcement();
                    a.setAnnouncementId(rs.getInt("announcement_id"));
                    a.setCourseId(rs.getInt("course_id"));
                    a.setInstructorId(rs.getInt("instructor_id"));
                    a.setTitle(rs.getString("title"));
                    a.setContent(rs.getString("content"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setTargetRole(rs.getString("target_role"));
                    String cname = rs.getString("course_name");
                    a.setCourseName(cname == null ? "System" : cname);
                    a.setInstructorName(rs.getString("instructor_name"));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Announcement> getGlobalAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as instructor_name FROM Announcements a " +
                     "JOIN Users u ON u.user_id = a.instructor_id " +
                     "WHERE a.course_id IS NULL ORDER BY a.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Announcement a = new Announcement();
                a.setAnnouncementId(rs.getInt("announcement_id"));
                a.setCourseId(0);
                a.setInstructorId(rs.getInt("instructor_id"));
                a.setTitle(rs.getString("title"));
                a.setContent(rs.getString("content"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                a.setTargetRole(rs.getString("target_role"));
                a.setCourseName("System");
                a.setInstructorName(rs.getString("instructor_name"));
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(Announcement a) {
        String sql = "INSERT INTO Announcements (course_id, instructor_id, title, content, target_role, created_at) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (a.getCourseId() == 0) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, a.getCourseId());
            }
            ps.setInt(2, a.getInstructorId());
            ps.setString(3, a.getTitle());
            ps.setString(4, a.getContent());
            ps.setString(5, a.getTargetRole() != null ? a.getTargetRole() : "all");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id, int instructorId) {
        String sql = "DELETE FROM Announcements WHERE announcement_id = ? AND instructor_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, instructorId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
