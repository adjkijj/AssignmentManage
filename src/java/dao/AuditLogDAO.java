package dao;

import config.DBContext;
import model.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public void insertLog(AuditLog log) {
        String sql = "INSERT INTO AuditLog (user_id, username, action, entity_type, entity_id, description, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getUserId() != null) ps.setInt(1, log.getUserId());
            else ps.setNull(1, Types.INTEGER);
            
            ps.setString(2, log.getUsername());
            ps.setString(3, log.getAction());
            ps.setString(4, log.getEntityType());
            
            if (log.getEntityId() != null) ps.setInt(5, log.getEntityId());
            else ps.setNull(5, Types.INTEGER);
            
            ps.setString(6, log.getDescription());
            ps.setString(7, log.getIpAddress());
            
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AuditLog> findAll(String actionFilter, String usernameFilter, String dateFrom, String dateTo, int offset, int pageSize) {
        List<AuditLog> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM AuditLog WHERE 1=1 ");
        if (actionFilter != null && !actionFilter.isEmpty()) sql.append(" AND action = ? ");
        if (usernameFilter != null && !usernameFilter.isEmpty()) sql.append(" AND username LIKE ? ");
        if (dateFrom != null && !dateFrom.isEmpty()) sql.append(" AND CAST(created_at AS DATE) >= ? ");
        if (dateTo != null && !dateTo.isEmpty()) sql.append(" AND CAST(created_at AS DATE) <= ? ");
        
        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int index = 1;
            if (actionFilter != null && !actionFilter.isEmpty()) ps.setString(index++, actionFilter);
            if (usernameFilter != null && !usernameFilter.isEmpty()) ps.setString(index++, "%" + usernameFilter + "%");
            if (dateFrom != null && !dateFrom.isEmpty()) ps.setString(index++, dateFrom);
            if (dateTo != null && !dateTo.isEmpty()) ps.setString(index++, dateTo);
            
            ps.setInt(index++, offset);
            ps.setInt(index++, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll(String actionFilter, String usernameFilter, String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM AuditLog WHERE 1=1 ");
        if (actionFilter != null && !actionFilter.isEmpty()) sql.append(" AND action = ? ");
        if (usernameFilter != null && !usernameFilter.isEmpty()) sql.append(" AND username LIKE ? ");
        if (dateFrom != null && !dateFrom.isEmpty()) sql.append(" AND CAST(created_at AS DATE) >= ? ");
        if (dateTo != null && !dateTo.isEmpty()) sql.append(" AND CAST(created_at AS DATE) <= ? ");
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int index = 1;
            if (actionFilter != null && !actionFilter.isEmpty()) ps.setString(index++, actionFilter);
            if (usernameFilter != null && !usernameFilter.isEmpty()) ps.setString(index++, "%" + usernameFilter + "%");
            if (dateFrom != null && !dateFrom.isEmpty()) ps.setString(index++, dateFrom);
            if (dateTo != null && !dateTo.isEmpty()) ps.setString(index++, dateTo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // backwards compatibility for existing code that logs actions
    public void logAction(int userId, String action, int targetId) {
        String sql = "INSERT INTO AuditLog (user_id, username, action, entity_type, entity_id, description, ip_address) " +
                     "VALUES (?, (SELECT username FROM Users WHERE user_id = ?), ?, 'Legacy', ?, 'Legacy log', NULL)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setString(3, action);
            ps.setInt(4, targetId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // backwards compatibility for dashboard which reads recent logs using action
    public List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM AuditLog ORDER BY created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<AuditLog> getAllLogs() {
        return findAll(null, null, null, null, 0, 1000);
    }

    private AuditLog mapResultSetToLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getObject("user_id") != null ? rs.getInt("user_id") : null);
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(rs.getObject("entity_id") != null ? rs.getInt("entity_id") : null);
        log.setDescription(rs.getString("description"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
