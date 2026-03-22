package model;

import java.util.Date;

/**
 * Semester model representing an academic semester.
 * Each semester has a name, start/end dates, and active status.
 */
public class Semester {
    private int semesterId;
    private String semesterName;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    public Semester() {}

    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
