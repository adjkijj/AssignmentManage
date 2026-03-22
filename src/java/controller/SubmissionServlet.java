package controller;

import dao.AssignmentDAO;
import dao.AuditLogDAO;
import dao.FeedbackTemplateDAO;
import dao.NotificationDAO;
import dao.SubmissionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Assignment;
import model.Notification;
import model.Submission;
import model.User;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SubmissionServlet - Handles file submissions (students) and grading (instructors).
 * Extended: version control, deadline enforcement, notification on grading, audit logging.
 * Actions: submit, grade, grades, history, view, sendReminder
 */
public class SubmissionServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/uploads/submissions/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");
        if (action == null) action = "grades";

        SubmissionDAO submissionDAO = new SubmissionDAO();

        switch (action) {
            case "submit":
                if ("student".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);

                    // Check hard deadline - block if passed
                    if (assignment.getHardDeadline() != null && new Date().after(assignment.getHardDeadline())) {
                        request.setAttribute("error", "The hard deadline has passed. Submissions are blocked.");
                        request.setAttribute("assignment", assignment);
                        request.getRequestDispatcher("/student/submit.jsp").forward(request, response);
                        return;
                    }

                    Submission existing = submissionDAO.getSubmission(assignmentId, currentUser.getUserId());
                    List<Submission> history = submissionDAO.getSubmissionHistory(currentUser.getUserId(), assignmentId);
                    request.setAttribute("assignment", assignment);
                    request.setAttribute("submission", existing);
                    request.setAttribute("submissionHistory", history);
                    request.getRequestDispatcher("/student/submit.jsp").forward(request, response);
                    return;
                }
                break;

            case "history":
                if ("student".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    List<Submission> history = submissionDAO.getSubmissionHistory(currentUser.getUserId(), assignmentId);
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    request.setAttribute("assignment", assignmentDAO.getAssignmentById(assignmentId));
                    request.setAttribute("submissionHistory", history);
                    request.getRequestDispatcher("/student/submission-history.jsp").forward(request, response);
                    return;
                }
                break;

            case "view":
                if ("instructor".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);
                    List<Submission> submissions = submissionDAO.getSubmissionsByAssignment(assignmentId);
                    request.setAttribute("assignment", assignment);
                    request.setAttribute("submissions", submissions);

                    // Feature 4: Count students not submitted for reminder button
                    List<model.User> notSubmitted = submissionDAO.getStudentsNotSubmitted(assignmentId, assignment.getCourseId());
                    request.setAttribute("notSubmittedCount", notSubmitted.size());

                    // Feature 6: Feedback templates
                    FeedbackTemplateDAO templateDAO = new FeedbackTemplateDAO();
                    request.setAttribute("feedbackTemplates", templateDAO.getTemplates(currentUser.getUserId()));

                    request.getRequestDispatcher("/instructor/grade-submissions.jsp").forward(request, response);
                    return;
                }
                break;

            case "sendReminder":
                if ("instructor".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);

                    // Ownership check
                    if (assignment == null || assignment.getInstructorId() != currentUser.getUserId()) {
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }

                    List<model.User> notSubmitted = submissionDAO.getStudentsNotSubmitted(assignmentId, assignment.getCourseId());
                    request.setAttribute("assignment", assignment);
                    request.setAttribute("notSubmittedStudents", notSubmitted);

                    // Default message
                    String defaultTitle = "Nhắc nhở: " + assignment.getTitle() + " sắp hết hạn";
                    String defaultMessage = "Bạn chưa nộp bài " + assignment.getTitle()
                            + " thuộc môn " + assignment.getCourseName()
                            + ". Deadline: " + assignment.getDeadline()
                            + ". Vui lòng nộp bài trước hạn.";
                    request.setAttribute("defaultTitle", defaultTitle);
                    request.setAttribute("defaultMessage", defaultMessage);

                    request.getRequestDispatcher("/instructor/send-reminder.jsp").forward(request, response);
                    return;
                }
                break;

            case "stats":
                if ("instructor".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);
                    if (assignment == null || assignment.getInstructorId() != currentUser.getUserId()) {
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }
                    model.AssignmentStats stats = submissionDAO.getAssignmentStats(assignmentId, assignment.getCourseId());
                    // Top 5 / Bottom 5
                    List<Submission> topList = submissionDAO.getSubmissionsByAssignment(assignmentId);
                    List<model.User> notSubmitted = submissionDAO.getStudentsNotSubmitted(assignmentId, assignment.getCourseId());
                    request.setAttribute("assignment", assignment);
                    request.setAttribute("stats", stats);
                    request.setAttribute("allSubmissions", topList);
                    request.setAttribute("notSubmittedStudents", notSubmitted);
                    request.getRequestDispatcher("/instructor/assignment-stats.jsp").forward(request, response);
                    return;
                }
                break;

            case "gradeQueue":
                if ("instructor".equals(currentUser.getRole())) {
                    int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                    int index = 0;
                    try { index = Integer.parseInt(request.getParameter("index")); } catch (Exception ignored) {}
                    AssignmentDAO assignmentDAO = new AssignmentDAO();
                    Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);
                    if (assignment == null || assignment.getInstructorId() != currentUser.getUserId()) {
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }
                    List<Submission> queue = submissionDAO.getSubmissionsByAssignment(assignmentId);
                    // Sort: ungraded first, then graded
                    queue.sort((a, b) -> {
                        boolean aGraded = a.getGrade() != null;
                        boolean bGraded = b.getGrade() != null;
                        if (aGraded != bGraded) return aGraded ? 1 : -1;
                        return 0;
                    });
                    if (index < 0) index = 0;
                    if (index >= queue.size()) index = queue.size() - 1;
                    Submission currentSub = queue.isEmpty() ? null : queue.get(index);
                    request.setAttribute("assignment", assignment);
                    request.setAttribute("currentSubmission", currentSub);
                    request.setAttribute("currentIndex", index);
                    request.setAttribute("totalCount", queue.size());
                    request.setAttribute("hasPrev", index > 0);
                    request.setAttribute("hasNext", index < queue.size() - 1);
                    // Feature 6: Feedback templates
                    dao.FeedbackTemplateDAO templateDAO = new dao.FeedbackTemplateDAO();
                    request.setAttribute("feedbackTemplates", templateDAO.getTemplates(currentUser.getUserId()));
                    request.getRequestDispatcher("/instructor/grade-queue.jsp").forward(request, response);
                    return;
                }
                break;

            case "grades":
            default:
                if ("student".equals(currentUser.getRole())) {
                    request.setAttribute("submissions",
                        submissionDAO.getSubmissionsByStudent(currentUser.getUserId()));
                    request.getRequestDispatcher("/student/grades.jsp").forward(request, response);
                    return;
                }
                break;
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");

        if ("submit".equals(action) && "student".equals(currentUser.getRole())) {
            handleSubmission(request, response, currentUser);
        } else if ("grade".equals(action) && "instructor".equals(currentUser.getRole())) {
            handleGrading(request, response, currentUser);
        } else if ("sendReminder".equals(action) && "instructor".equals(currentUser.getRole())) {
            handleSendReminder(request, response, currentUser);
        } else if ("gradeQueueSave".equals(action) && "instructor".equals(currentUser.getRole())) {
            handleGradeQueueSave(request, response, currentUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleSubmission(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        try {
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));

            // Get assignment for deadline checking
            AssignmentDAO assignmentDAO = new AssignmentDAO();
            Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);

            // Hard deadline check - block submission
            if (assignment.getHardDeadline() != null && new Date().after(assignment.getHardDeadline())) {
                request.setAttribute("error", "The hard deadline has passed. Submissions are blocked.");
                request.setAttribute("assignment", assignment);
                request.getRequestDispatcher("/student/submit.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Please select a file to submit.");
                doGet(request, response);
                return;
            }

            // ===== Feature 2: Server-side file type validation =====
            String originalFileName = getFileName(filePart);
            String validationError = util.FileValidationUtil.validate(
                    originalFileName, filePart.getInputStream());
            if (validationError != null) {
                java.util.logging.Logger.getLogger("SubmissionServlet").warning(
                    "[BLOCKED UPLOAD] user=" + currentUser.getUserId()
                    + " file=" + originalFileName);
                request.setAttribute("error", validationError);
                doGet(request, response);
                return;
            }
            // ===== End validation =====

            String fileName = currentUser.getUserId() + "_" + assignmentId + "_"
                             + System.currentTimeMillis() + "_" + originalFileName;
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            filePart.write(UPLOAD_DIR + fileName);

            SubmissionDAO submissionDAO = new SubmissionDAO();

            // Always create a new version (multi-submission support)
            int nextVersion = submissionDAO.getLatestVersion(currentUser.getUserId(), assignmentId) + 1;

            // Check soft deadline - mark late
            boolean isLate = false;
            String status = "submitted";
            if (assignment.getSoftDeadline() != null && new Date().after(assignment.getSoftDeadline())) {
                isLate = true;
                status = "late";
            }

            Submission submission = new Submission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(currentUser.getUserId());
            submission.setFilePath(fileName);
            submission.setVersion(nextVersion);
            submission.setStatus(status);
            submission.setIsLate(isLate);
            submissionDAO.submitAssignment(submission);

            response.sendRedirect(request.getContextPath() + "/submissions?action=grades");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Submission failed: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleGrading(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        try {
            int submissionId = Integer.parseInt(request.getParameter("submissionId"));
            double grade = Double.parseDouble(request.getParameter("grade"));
            String feedback = request.getParameter("feedback");
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));

            SubmissionDAO submissionDAO = new SubmissionDAO();
            Submission sub = submissionDAO.getSubmissionById(submissionId);
            submissionDAO.gradeSubmission(submissionId, grade, feedback);

            // Audit log: grade updated
            AuditLogDAO auditDao = new AuditLogDAO();
            auditDao.logAction(currentUser.getUserId(), "UPDATE_GRADE", submissionId);

            // Notify student
            if (sub != null) {
                Notification n = new Notification();
                n.setUserId(sub.getStudentId());
                n.setTitle("Assignment Graded");
                n.setMessage("Your submission has been graded. Score: " + grade);
                new NotificationDAO().addNotification(n);
            }

            response.sendRedirect(request.getContextPath() + "/submissions?action=view&assignmentId=" + assignmentId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * Feature 4: Handle sending reminders to students who haven't submitted.
     */
    private void handleSendReminder(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        try {
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            String title = request.getParameter("reminderTitle");
            String message = request.getParameter("reminderMessage");

            AssignmentDAO assignmentDAO = new AssignmentDAO();
            Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);

            // Ownership check
            if (assignment == null || assignment.getInstructorId() != currentUser.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            SubmissionDAO submissionDAO = new SubmissionDAO();
            List<model.User> notSubmitted = submissionDAO.getStudentsNotSubmitted(assignmentId, assignment.getCourseId());

            if (!notSubmitted.isEmpty()) {
                List<Integer> userIds = new ArrayList<>();
                for (model.User u : notSubmitted) {
                    userIds.add(u.getUserId());
                }
                new NotificationDAO().createBulkNotifications(userIds, title, message);

                // Audit log
                new AuditLogDAO().logAction(currentUser.getUserId(), "SEND_REMINDER", assignmentId);
            }

            response.sendRedirect(request.getContextPath() + "/submissions?action=view&assignmentId=" + assignmentId
                + "&reminderSent=" + notSubmitted.size());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * Feature 8: Save grade from queue and navigate to next.
     */
    private void handleGradeQueueSave(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        try {
            int submissionId = Integer.parseInt(request.getParameter("submissionId"));
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            int nextIndex = Integer.parseInt(request.getParameter("nextIndex"));
            double grade = Double.parseDouble(request.getParameter("grade"));
            String feedback = request.getParameter("feedback");

            SubmissionDAO submissionDAO = new SubmissionDAO();
            Submission sub = submissionDAO.getSubmissionById(submissionId);
            submissionDAO.gradeSubmission(submissionId, grade, feedback);

            new AuditLogDAO().logAction(currentUser.getUserId(), "UPDATE_GRADE", submissionId);

            if (sub != null) {
                Notification n = new Notification();
                n.setUserId(sub.getStudentId());
                n.setTitle("Assignment Graded");
                n.setMessage("Your submission has been graded. Score: " + grade);
                new NotificationDAO().addNotification(n);
            }

            response.sendRedirect(request.getContextPath() + "/submissions?action=gradeQueue&assignmentId=" + assignmentId + "&index=" + nextIndex);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "unknown";
    }
}


