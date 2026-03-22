package dao;

import config.DBContext;
import java.sql.*;
import java.util.Date;

/**
 * DAO for password reset token operations.
 */
public class PasswordResetTokenDAO {

    public boolean createToken(int userId, String token, Date expiresAt) {
        String sql = "INSERT INTO PasswordResetTokens (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, new Timestamp(expiresAt.getTime()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Find a valid (not used, not expired) token.
     * @return userId if found, -1 otherwise
     */
    public int findValidToken(String token) {
        String sql = "SELECT user_id FROM PasswordResetTokens "
                   + "WHERE token = ? AND is_used = 0 AND expires_at > GETDATE()";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public boolean markUsed(String token) {
        String sql = "UPDATE PasswordResetTokens SET is_used = 1 WHERE token = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
