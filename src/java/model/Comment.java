package model;

import java.util.Date;

/**
 * Comment model representing a comment on an assignment.
 * Extended: parent_comment_id for threading/replies.
 */
public class Comment {
    private int commentId;
    private int assignmentId;
    private int userId;
    private String content;
    private Integer parentCommentId; // nullable - for reply threading
    private Date createdAt;
    private String userName; // joined field

    public Comment() {}

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
