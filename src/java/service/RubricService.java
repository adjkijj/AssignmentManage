package service;

import dao.RubricDAO;
import dao.SubmissionDAO;
import model.Rubric;
import model.RubricCriteria;
import model.RubricGrade;
import model.Submission;
import java.util.List;

public class RubricService {

    private RubricDAO rubricDAO = new RubricDAO();
    private SubmissionDAO submissionDAO = new SubmissionDAO();

    public boolean validateWeightsSum100(List<RubricCriteria> criteriaList) {
        double sum = 0;
        for (RubricCriteria rc : criteriaList) {
            sum += rc.getWeightPercent();
        }
        return Math.abs(sum - 100.0) < 0.01;
    }

    public double calculateFinalGrade(int submissionId, int rubricId) {
        Submission sub = submissionDAO.getSubmissionById(submissionId);
        if (sub == null) return 0;
        
        Rubric r = rubricDAO.findByAssignment(sub.getAssignmentId());
        if (r == null || r.getId() != rubricId) return 0;

        List<RubricCriteria> criteriaList = rubricDAO.findCriteriaByRubric(rubricId);
        List<RubricGrade> grades = rubricDAO.findGradesBySubmission(submissionId);
        
        double finalPercentage = 0;
        for (RubricCriteria rc : criteriaList) {
            for (RubricGrade rg : grades) {
                if (rg.getCriteriaId() == rc.getId()) {
                    double criteriaScore = 0;
                    if (rc.getMaxPoints() > 0) {
                        criteriaScore = (rg.getPointsEarned() / rc.getMaxPoints()) * (rc.getWeightPercent() / 100.0);
                    }
                    finalPercentage += criteriaScore;
                }
            }
        }
        
        double finalGrade = finalPercentage * r.getTotalPoints();
        
        // Round to 2 decimal places
        finalGrade = Math.round(finalGrade * 100.0) / 100.0;
        
        // Update submission
        submissionDAO.gradeSubmission(submissionId, finalGrade, "Graded via Rubric");
        return finalGrade;
    }
}
