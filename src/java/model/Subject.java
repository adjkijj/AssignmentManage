package model;

import java.util.Date;

/**
 * Subject model representing an academic subject (e.g., PRJ301).
 * A subject can have multiple classes across different semesters.
 */
public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private String description;
    private Date createdAt;

    public Subject() {}

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
