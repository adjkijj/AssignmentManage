package model;

public class RubricCriteria {
    private int id;
    private int rubricId;
    private String criteriaName;
    private String description;
    private double maxPoints;
    private double weightPercent;

    public RubricCriteria() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRubricId() { return rubricId; }
    public void setRubricId(int rubricId) { this.rubricId = rubricId; }
    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getMaxPoints() { return maxPoints; }
    public void setMaxPoints(double maxPoints) { this.maxPoints = maxPoints; }
    public double getWeightPercent() { return weightPercent; }
    public void setWeightPercent(double weightPercent) { this.weightPercent = weightPercent; }
}
