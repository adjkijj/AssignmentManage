package model;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private Integer userId;
    private String username;
    private String action;
    private String entityType;
    private Integer entityId;
    private String description;
    private String ipAddress;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // For backwards compatibility with old audit log calls if any
    public int getLogId() { return id; }
    public void setLogId(int id) { this.id = id; }
    public int getTargetId() { return entityId != null ? entityId : 0; }
    public void setTargetId(int targetId) { this.entityId = targetId; }
}
