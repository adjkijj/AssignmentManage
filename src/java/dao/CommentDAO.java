package dao;

import config.DBContext;
import model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Comment operations.
 * Extended: threaded comments with parent_comment_id support.
 */
public class CommentDAO {

    /**
     * Get all comments for an assignment (flat list, all levels).
     */
    public List<Comment> getCommentsByAssignment(int assignmentId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS user_name FROM Comments c "
                   + "JOIN Users u ON c.user_id = u.user_id "
                   + "WHERE c.assignment_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get top-level comments only (no parent).
     */
    public List<Comment> getTopLevelComments(int assignmentId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS user_name FROM Comments c "
                   + "JOIN Users u ON c.user_id = u.user_id "
                   + "WHERE c.assignment_id = ? AND c.parent_comment_id IS NULL "
                   + "ORDER BY c.created_at ASC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get replies to a specific comment.
     */
    public List<Comment> getReplies(int parentCommentId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS user_name FROM Comments c "
                   + "JOIN Users u ON c.user_id = u.user_id "
                   + "WHERE c.parent_comment_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentCommentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Create a new comment (supports threading via parentCommentId).
     */
    public boolean createComment(Comment comment) {
        String sql = "INSERT INTO Comments (assignment_id, user_id, content, parent_comment_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comment.getAssignmentId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getContent());
            if (comment.getParentCommentId() != null) {
                ps.setInt(4, comment.getParentCommentId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a comment.
     */
    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM Comments WHERE comment_id = ? AND user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setCommentId(rs.getInt("comment_id"));
        c.setAssignmentId(rs.getInt("assignment_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setContent(rs.getString("content"));
        int parentId = rs.getInt("parent_comment_id");
        if (!rs.wasNull()) {
            c.setParentCommentId(parentId);
        }
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUserName(rs.getString("user_name"));
        return c;
    }
}

