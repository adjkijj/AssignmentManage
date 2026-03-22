package dao;

import config.DBContext;
import model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Notification operations.
 * Supports creating, reading, and marking notifications as read.
 */
public class NotificationDAO {

    /**
     * Add a new notification.
     */
    public boolean addNotification(Notification n) {
        String sql = "INSERT INTO Notifications (user_id, title, message) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get unread notifications for a user.
     */
    public List<Notification> getUnreadByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotification(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get recent notifications for a user (read + unread), limited.
     */
    public List<Notification> getRecentByUser(int userId, int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM Notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotification(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mark a notification as read.
     */
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE notification_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mark all notifications as read for a user.
     */
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Count unread notifications for a user.
     */
    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Send notification to all students enrolled in a course.
     */
    public void notifyStudentsInCourse(int courseId, String title, String message) {
        String sql = "INSERT INTO Notifications (user_id, title, message) "
                   + "SELECT student_id, ?, ? FROM Enrollments WHERE course_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, message);
            ps.setInt(3, courseId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send notification to all users in the system (Admins).
     */
    public void notifyAllUsers(String title, String message) {
        String sql = "INSERT INTO Notifications (user_id, title, message) "
                   + "SELECT user_id, ?, ? FROM Users";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send notification to all instructors in the system.
     */
    public void notifyAllInstructors(String title, String message) {
        String sql = "INSERT INTO Notifications (user_id, title, message) "
                   + "SELECT user_id, ?, ? FROM Users WHERE role = 'instructor'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("notification_id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setIsRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }

    /**
     * Feature 4: Create notifications for a list of user IDs (batch insert).
     */
    public void createBulkNotifications(java.util.List<Integer> userIds, String title, String message) {
        String sql = "INSERT INTO Notifications (user_id, title, message) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int userId : userIds) {
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, message);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a notification permanently.
     */
    public void deleteNotification(int id) throws Exception {
        String sql = "DELETE FROM Notifications WHERE notification_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
