package model;

import java.util.Date;

/**
 * Notification model for the notification system.
 * Stores alerts for users (e.g., assignment created, graded, near deadline).
 */
public class Notification {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private boolean isRead;
    private Date createdAt;

    public Notification() {
        this.isRead = false;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
