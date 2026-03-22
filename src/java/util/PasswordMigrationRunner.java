package util;

import config.DBContext;
import java.sql.*;

/**
 * One-time migration tool: hashes all existing plain-text passwords.
 * Run from command line: java -cp "WEB-INF/classes;WEB-INF/lib/*" util.PasswordMigrationRunner
 * Delete this class after running.
 */
public class PasswordMigrationRunner {

    public static void main(String[] args) {
        System.out.println("=== Password Migration: Plain Text → PBKDF2 ===");
        int updated = 0;
        int skipped = 0;

        try (Connection conn = DBContext.getConnection()) {
            // Read all users
            String selectSql = "SELECT user_id, password FROM Users";
            String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";

            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String currentPassword = rs.getString("password");

                    // Skip if already hashed (contains colon separator)
                    if (currentPassword.contains(":")) {
                        System.out.println("  SKIP user_id=" + userId + " (already hashed)");
                        skipped++;
                        continue;
                    }

                    String hashed = PasswordUtil.hashPassword(currentPassword);
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, hashed);
                        updatePs.setInt(2, userId);
                        updatePs.executeUpdate();
                        System.out.println("  DONE user_id=" + userId);
                        updated++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=== Migration complete: " + updated + " updated, " + skipped + " skipped ===");
    }
}
