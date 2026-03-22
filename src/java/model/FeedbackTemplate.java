package model;

import java.util.Date;

/**
 * FeedbackTemplate model — predefined feedback snippets for grading — Feature 6.
 */
public class FeedbackTemplate {
    private int templateId;
    private Integer instructorId; // null = shared template
    private String category;      // positive, negative, neutral
    private String content;
    private Date createdAt;

    public FeedbackTemplate() {}

    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }

    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
