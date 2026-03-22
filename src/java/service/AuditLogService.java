package service;

import dao.AuditLogDAO;
import model.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public class AuditLogService {

    private AuditLogDAO logDAO = new AuditLogDAO();

    public void log(HttpServletRequest req, int userId, String username, String action, String entityType, Integer entityId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        
        // Extract IP Address from request
        String ipAddress = req.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
        }
        // Handle case where multiple IPs are returned in X-Forwarded-For
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        auditLog.setIpAddress(ipAddress);
        
        logDAO.insertLog(auditLog);
    }
    
    public void log(Integer userId, String username, String action, String entityType, Integer entityId, String description, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        logDAO.insertLog(auditLog);
    }

    public List<AuditLog> getLogsPagedFiltered(String actionFilter, String usernameFilter, String dateFrom, String dateTo, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return logDAO.findAll(actionFilter, usernameFilter, dateFrom, dateTo, offset, pageSize);
    }

    public int getTotalPages(String actionFilter, String usernameFilter, String dateFrom, String dateTo, int pageSize) {
        int totalRecords = logDAO.countAll(actionFilter, usernameFilter, dateFrom, dateTo);
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
    
    public int getTotalRecords(String actionFilter, String usernameFilter, String dateFrom, String dateTo) {
        return logDAO.countAll(actionFilter, usernameFilter, dateFrom, dateTo);
    }
}
