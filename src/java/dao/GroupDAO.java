package dao;

import config.DBContext;
import model.Group;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Group Assignment operations.
 * Manages groups, membership, and group-based lookups.
 */
public class GroupDAO {

    /**
     * Create a new group and return the generated ID.
     */
    public int createGroup(Group group) {
        String sql = "INSERT INTO Groups (assignment_id, group_name) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, group.getAssignmentId());
            ps.setString(2, group.getGroupName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Add a student member to a group.
     */
    public boolean addMemberToGroup(int groupId, int studentId) {
        String sql = "INSERT INTO Group_Members (group_id, student_id) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a student from a group.
     */
    public boolean removeMemberFromGroup(int groupId, int studentId) {
        String sql = "DELETE FROM Group_Members WHERE group_id = ? AND student_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the group a student belongs to for a specific assignment.
     */
    public Group getGroupForStudent(int studentId, int assignmentId) {
        String sql = "SELECT g.* FROM Groups g "
                   + "JOIN Group_Members gm ON g.group_id = gm.group_id "
                   + "WHERE gm.student_id = ? AND g.assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Group g = mapResultSetToGroup(rs);
                    g.setMembers(getGroupMembers(g.getGroupId()));
                    return g;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all groups for an assignment.
     */
    public List<Group> getGroupsByAssignment(int assignmentId) {
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM Groups WHERE assignment_id = ? ORDER BY group_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Group g = mapResultSetToGroup(rs);
                    g.setMembers(getGroupMembers(g.getGroupId()));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get members of a group.
     */
    public List<User> getGroupMembers(int groupId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u "
                   + "JOIN Group_Members gm ON u.user_id = gm.student_id "
                   + "WHERE gm.group_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setFullName(rs.getString("full_name"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    members.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * Get a group by ID.
     */
    public Group getGroupById(int groupId) {
        String sql = "SELECT * FROM Groups WHERE group_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Group g = mapResultSetToGroup(rs);
                    g.setMembers(getGroupMembers(g.getGroupId()));
                    return g;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Group mapResultSetToGroup(ResultSet rs) throws SQLException {
        Group g = new Group();
        g.setGroupId(rs.getInt("group_id"));
        g.setAssignmentId(rs.getInt("assignment_id"));
        g.setGroupName(rs.getString("group_name"));
        g.setCreatedAt(rs.getTimestamp("created_at"));
        return g;
    }
}
