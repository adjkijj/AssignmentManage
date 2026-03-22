package controller;

import dao.AssignmentDAO;
import dao.RubricDAO;
import dao.SubmissionDAO;
import model.Assignment;
import model.Rubric;
import model.RubricCriteria;
import model.RubricGrade;
import model.Submission;
import service.RubricService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RubricServlet extends HttpServlet {

    private RubricDAO rubricDAO = new RubricDAO();
    private RubricService rubricService = new RubricService();
    private AssignmentDAO assignmentDAO = new AssignmentDAO();
    private SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "view";

        try {
            switch (action) {
                case "edit": // Edit or create rubric for an assignment
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    Assignment a = assignmentDAO.getAssignmentById(assignmentId);
                    Rubric r = rubricDAO.findByAssignment(assignmentId);
                    if (r != null) {
                        List<RubricCriteria> criteria = rubricDAO.findCriteriaByRubric(r.getId());
                        request.setAttribute("rubric", r);
                        request.setAttribute("criteriaList", criteria);
                    }
                    request.setAttribute("assignment", a);
                    request.getRequestDispatcher("/instructor/rubric-create.jsp").forward(request, response);
                    break;
                case "grade": // Grade a submission using rubric
                    int submissionId = Integer.parseInt(request.getParameter("submissionId"));
                    Submission sub = submissionDAO.getSubmissionById(submissionId);
                    Rubric rubric = rubricDAO.findByAssignment(sub.getAssignmentId());
                    if (rubric == null) {
                        response.sendRedirect(request.getContextPath() + "/submissions?action=view&assignmentId=" + sub.getAssignmentId() + "&error=NoRubric");
                        return;
                    }
                    List<RubricCriteria> clist = rubricDAO.findCriteriaByRubric(rubric.getId());
                    List<RubricGrade> glides = rubricDAO.findGradesBySubmission(submissionId);
                    
                    request.setAttribute("submission", sub);
                    request.setAttribute("rubric", rubric);
                    request.setAttribute("criteriaList", clist);
                    request.setAttribute("grades", glides); // to prepopulate form
                    request.getRequestDispatcher("/instructor/rubric-grade.jsp").forward(request, response);
                    break;
                case "view": // Student views rubric grade
                    int sId = Integer.parseInt(request.getParameter("submissionId"));
                    Submission s = submissionDAO.getSubmissionById(sId);
                    Rubric rub = rubricDAO.findByAssignment(s.getAssignmentId());
                    if (rub != null) {
                        List<RubricCriteria> rcList = rubricDAO.findCriteriaByRubric(rub.getId());
                        List<RubricGrade> rgList = rubricDAO.findGradesBySubmission(sId);
                        request.setAttribute("rubric", rub);
                        request.setAttribute("criteriaList", rcList);
                        request.setAttribute("grades", rgList);
                    }
                    request.setAttribute("submission", s);
                    request.getRequestDispatcher("/student/rubric-view.jsp").forward(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("save".equals(action)) {
            // Save rubric definition
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            double totalPoints = Double.parseDouble(request.getParameter("totalPoints"));
            
            Rubric r = rubricDAO.findByAssignment(assignmentId);
            int rubricId;
            if (r == null) {
                r = new Rubric();
                r.setAssignmentId(assignmentId);
                r.setTotalPoints(totalPoints);
                rubricId = rubricDAO.saveRubric(r);
            } else {
                r.setTotalPoints(totalPoints);
                rubricDAO.updateRubric(r);
                rubricId = r.getId();
                rubricDAO.clearCriteriaByRubric(rubricId); // clear old criteria
            }
            
            String[] cNames = request.getParameterValues("criteriaName");
            String[] cDescs = request.getParameterValues("description");
            String[] maxPts = request.getParameterValues("maxPoints");
            String[] weights = request.getParameterValues("weightPercent");
            
            if (cNames != null) {
                List<RubricCriteria> listToSave = new ArrayList<>();
                for (int i = 0; i < cNames.length; i++) {
                    RubricCriteria rc = new RubricCriteria();
                    rc.setRubricId(rubricId);
                    rc.setCriteriaName(cNames[i]);
                    rc.setDescription(cDescs[i]);
                    rc.setMaxPoints(Double.parseDouble(maxPts[i]));
                    rc.setWeightPercent(Double.parseDouble(weights[i]));
                    listToSave.add(rc);
                }
                
                if (!rubricService.validateWeightsSum100(listToSave)) {
                    // Could redirect back with error
                }
                
                for (RubricCriteria rc : listToSave) {
                    rubricDAO.saveCriteria(rc);
                }
            }
            response.sendRedirect(request.getContextPath() + "/assignments?success=RubricSaved");

        } else if ("saveGrade".equals(action)) {
            // Save grades for a submission
            int submissionId = Integer.parseInt(request.getParameter("submissionId"));
            int rubricId = Integer.parseInt(request.getParameter("rubricId"));
            
            rubricDAO.clearGradesBySubmission(submissionId);
            
            String[] criteriaIds = request.getParameterValues("criteriaId");
            if (criteriaIds != null) {
                for (String cIdStr : criteriaIds) {
                    int cId = Integer.parseInt(cIdStr);
                    double pts = Double.parseDouble(request.getParameter("points_" + cId));
                    String comment = request.getParameter("comment_" + cId);
                    
                    RubricGrade rg = new RubricGrade();
                    rg.setSubmissionId(submissionId);
                    rg.setCriteriaId(cId);
                    rg.setPointsEarned(pts);
                    rg.setComment(comment);
                    rubricDAO.saveRubricGrade(rg);
                }
            }
            
            // Calc final grade
            rubricService.calculateFinalGrade(submissionId, rubricId);
            
            Submission sub = submissionDAO.getSubmissionById(submissionId);
            response.sendRedirect(request.getContextPath() + "/submissions?action=view&assignmentId=" + sub.getAssignmentId() + "&success=Graded");
        }
    }
}
