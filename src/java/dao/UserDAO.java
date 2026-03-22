package dao;

import config.DBContext;
import model.User;
import util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {

    /**
     * Authenticate a user by username and password.
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND is_active = 1";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    // Verify password using PBKDF2 (also supports legacy plain text)
                    if (PasswordUtil.verifyPassword(password, user.getPassword())) {
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Register a new user.
     */
    public boolean register(User user) {
        String sql = "INSERT INTO Users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user password (already hashed).
     */
    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get user by email (for forgot password).
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ? AND is_active = 1";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get user by ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all users by role.
     */
    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = ? ORDER BY full_name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Update user information.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET full_name = ?, email = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Toggle user active status.
     */
    public boolean toggleActive(int userId) {
        String sql = "UPDATE Users SET is_active = CASE WHEN is_active = 1 THEN 0 ELSE 1 END WHERE user_id = ?";
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
     * Check if username already exists.
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
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
     * Check if email already exists.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
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
     * Count users by role.
     */
    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM Users WHERE role = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Bulk insert users from CSV import.
     * Returns number of successfully inserted users.
     */
    public int insertUsersBatch(List<User> users) {
        String sql = "INSERT INTO Users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        int count = 0;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (User u : users) {
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getPassword());
                ps.setString(3, u.getFullName());
                ps.setString(4, u.getEmail());
                ps.setString(5, u.getRole());
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            conn.commit();
            for (int r : results) {
                if (r > 0) count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<User> searchUsers(String keyword, String role, String status, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Users WHERE 1=1 ");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasRole = (role != null && !role.trim().isEmpty());
        boolean hasStatus = (status != null && !status.trim().isEmpty());
        
        if (hasKeyword) sql.append(" AND (username LIKE ? OR full_name LIKE ? OR email LIKE ?) ");
        if (hasRole) sql.append(" AND role = ? ");
        if (hasStatus) sql.append(" AND is_active = ? ");
        
        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasKeyword) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(index++, k);
                ps.setString(index++, k);
                ps.setString(index++, k);
            }
            if (hasRole) ps.setString(index++, role.trim());
            if (hasStatus) ps.setInt(index++, status.equals("active") ? 1 : 0);
            
            ps.setInt(index++, offset);
            ps.setInt(index++, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countSearchUsers(String keyword, String role, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE 1=1 ");
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean hasRole = (role != null && !role.trim().isEmpty());
        boolean hasStatus = (status != null && !status.trim().isEmpty());
        
        if (hasKeyword) sql.append(" AND (username LIKE ? OR full_name LIKE ? OR email LIKE ?) ");
        if (hasRole) sql.append(" AND role = ? ");
        if (hasStatus) sql.append(" AND is_active = ? ");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasKeyword) {
                String k = "%" + keyword.trim() + "%";
                ps.setString(index++, k);
                ps.setString(index++, k);
                ps.setString(index++, k);
            }
            if (hasRole) ps.setString(index++, role.trim());
            if (hasStatus) ps.setInt(index++, status.equals("active") ? 1 : 0);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Delete a user. Throws exception if user has relational data (submissions, enrollments, etc).
     */
    public void deleteUser(int id) throws Exception {
        String sql = "DELETE FROM Users WHERE user_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setIsActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
