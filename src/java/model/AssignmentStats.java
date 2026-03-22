package model;

import java.util.List;

/**
 * Feature 10: Assignment statistics DTO.
 */
public class AssignmentStats {
    private int graded;
    private int totalSubmitted;
    private int totalEnrolled;
    private double mean;
    private double max;
    private double min;
    private double stdDev;
    private int passCount;
    private int lateCount;
    private List<int[]> distribution; // 10 buckets: [0]={count for 0-1}, [1]={count for 1-2}, ...

    public AssignmentStats() {}

    public int getGraded() { return graded; }
    public void setGraded(int graded) { this.graded = graded; }

    public int getTotalSubmitted() { return totalSubmitted; }
    public void setTotalSubmitted(int totalSubmitted) { this.totalSubmitted = totalSubmitted; }

    public int getTotalEnrolled() { return totalEnrolled; }
    public void setTotalEnrolled(int totalEnrolled) { this.totalEnrolled = totalEnrolled; }

    public double getMean() { return mean; }
    public void setMean(double mean) { this.mean = mean; }

    public double getMax() { return max; }
    public void setMax(double max) { this.max = max; }

    public double getMin() { return min; }
    public void setMin(double min) { this.min = min; }

    public double getStdDev() { return stdDev; }
    public void setStdDev(double stdDev) { this.stdDev = stdDev; }

    public int getPassCount() { return passCount; }
    public void setPassCount(int passCount) { this.passCount = passCount; }

    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }

    public List<int[]> getDistribution() { return distribution; }
    public void setDistribution(List<int[]> distribution) { this.distribution = distribution; }

    public double getPassRate() {
        return graded > 0 ? Math.round(passCount * 1000.0 / graded) / 10.0 : 0;
    }
    public double getLateRate() {
        return totalSubmitted > 0 ? Math.round(lateCount * 1000.0 / totalSubmitted) / 10.0 : 0;
    }
}
