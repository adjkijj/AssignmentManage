package model;

public class Rubric {
    private int id;
    private int assignmentId;
    private double totalPoints;

    public Rubric() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }
}
