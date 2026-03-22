package controller;

import dao.AssignmentDAO;
import dao.AuditLogDAO;
import dao.CommentDAO;
import dao.CourseDAO;
import dao.NotificationDAO;
import dao.SubmissionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Assignment;
import model.User;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AssignmentServlet - Handles assignment CRUD for instructors and viewing for students.
 * Actions: list, detail, create, edit, delete
 */
public class AssignmentServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/uploads/assignments/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");
        if (action == null) action = "list";

        AssignmentDAO assignmentDAO = new AssignmentDAO();
        CourseDAO courseDAO = new CourseDAO();

        switch (action) {
            case "detail":
                int id = Integer.parseInt(request.getParameter("id"));
                Assignment assignment = assignmentDAO.getAssignmentById(id);
                if (assignment != null) {
                    request.setAttribute("assignment", assignment);
                    CommentDAO commentDAO = new CommentDAO();
                    request.setAttribute("comments", commentDAO.getCommentsByAssignment(id));

                    if ("student".equals(currentUser.getRole())) {
                        SubmissionDAO submissionDAO = new SubmissionDAO();
                        request.setAttribute("submission",
                            submissionDAO.getSubmission(id, currentUser.getUserId()));
                        request.getRequestDispatcher("/student/assignments.jsp").forward(request, response);
                    } else {
                        SubmissionDAO submissionDAO = new SubmissionDAO();
                        request.setAttribute("submissions",
                            submissionDAO.getSubmissionsByAssignment(id));
                        request.getRequestDispatcher("/instructor/assignments.jsp").forward(request, response);
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/assignments");
                }
                return;

            case "create":
                if ("instructor".equals(currentUser.getRole())) {
                    request.setAttribute("courses", courseDAO.getCoursesByInstructor(currentUser.getUserId()));
                    request.getRequestDispatcher("/instructor/assignment-form.jsp").forward(request, response);
                    return;
                }
                break;

            case "edit":
                if ("instructor".equals(currentUser.getRole())) {
                    int editId = Integer.parseInt(request.getParameter("id"));
                    Assignment editAssignment = assignmentDAO.getAssignmentById(editId);
                    if (editAssignment != null && editAssignment.getInstructorId() == currentUser.getUserId()) {
                        request.setAttribute("assignment", editAssignment);
                        request.setAttribute("courses", courseDAO.getCoursesByInstructor(currentUser.getUserId()));
                        request.getRequestDispatcher("/instructor/assignment-form.jsp").forward(request, response);
                        return;
                    }
                }
                break;

            case "duplicateForm":
                if ("instructor".equals(currentUser.getRole())) {
                    int srcId = Integer.parseInt(request.getParameter("id"));
                    Assignment srcAssignment = assignmentDAO.getAssignmentById(srcId);
                    if (srcAssignment != null && srcAssignment.getInstructorId() == currentUser.getUserId()) {
                        request.setAttribute("sourceAssignment", srcAssignment);
                        java.util.List<model.Course> myCourses = courseDAO.getCoursesByInstructor(currentUser.getUserId());
                        // Remove source course from targets
                        myCourses.removeIf(c -> c.getCourseId() == srcAssignment.getCourseId());
                        request.setAttribute("targetCourses", myCourses);
                        request.getRequestDispatcher("/instructor/duplicate-form.jsp").forward(request, response);
                        return;
                    }
                }
                response.sendRedirect(request.getContextPath() + "/assignments");
                return;

            case "delete":
                if ("instructor".equals(currentUser.getRole())) {
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    Assignment deleteAssignment = assignmentDAO.getAssignmentById(deleteId);
                    if (deleteAssignment != null && deleteAssignment.getInstructorId() == currentUser.getUserId()) {
                        assignmentDAO.deleteAssignment(deleteId);
                        // Audit log: assignment deleted
                        new AuditLogDAO().logAction(currentUser.getUserId(), "DELETE_ASSIGNMENT", deleteId);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/assignments");
                return;
        }

        // Default: list assignments
        if ("student".equals(currentUser.getRole())) {
            // Feature 6: Read filter params
            String courseIdStr = request.getParameter("courseId");
            String status = request.getParameter("status");
            String keyword = request.getParameter("keyword");
            Integer filterCourseId = null;
            if (courseIdStr != null && !courseIdStr.isEmpty()) {
                try { filterCourseId = Integer.parseInt(courseIdStr); } catch (NumberFormatException ignored) {}
            }

            // Check if any filter is active
            boolean hasFilter = (filterCourseId != null || (status != null && !status.isEmpty()) || (keyword != null && !keyword.isEmpty()));

            if (hasFilter) {
                request.setAttribute("assignments", assignmentDAO.getAssignmentsWithFilter(
                    currentUser.getUserId(), filterCourseId, status, keyword));
            } else {
                request.setAttribute("assignments", assignmentDAO.getAssignmentsWithStatusByStudent(currentUser.getUserId()));
            }

            // Enrolled courses for filter dropdown
            request.setAttribute("enrolledCourses", courseDAO.getCoursesByStudent(currentUser.getUserId()));
            // Preserve filter values
            request.setAttribute("filterCourseId", courseIdStr);
            request.setAttribute("filterStatus", status);
            request.setAttribute("filterKeyword", keyword);

            request.getRequestDispatcher("/student/assignments.jsp").forward(request, response);
        } else if ("instructor".equals(currentUser.getRole())) {
            // Feature 2: Read instructor filter params
            String courseIdStr = request.getParameter("courseId");
            String gradingStatus = request.getParameter("gradingStatus");
            String keyword = request.getParameter("keyword");
            Integer filterCourseId = null;
            if (courseIdStr != null && !courseIdStr.isEmpty()) {
                try { filterCourseId = Integer.parseInt(courseIdStr); } catch (NumberFormatException ignored) {}
            }

            boolean hasFilter = (filterCourseId != null || (gradingStatus != null && !gradingStatus.isEmpty()) || (keyword != null && !keyword.isEmpty()));

            if (hasFilter) {
                request.setAttribute("assignments", assignmentDAO.getAssignmentsByInstructorWithFilter(
                    currentUser.getUserId(), filterCourseId, gradingStatus, keyword));
            } else {
                request.setAttribute("assignments", assignmentDAO.getAssignmentsByInstructorWithFilter(
                    currentUser.getUserId(), null, null, null));
            }

            // Instructor courses for filter dropdown
            request.setAttribute("instructorCourses", courseDAO.getCoursesByInstructor(currentUser.getUserId()));
            request.setAttribute("filterCourseId", courseIdStr);
            request.setAttribute("filterGradingStatus", gradingStatus);
            request.setAttribute("filterKeyword", keyword);

            // Feature 3: Submission stats map
            SubmissionDAO submissionDAO = new SubmissionDAO();
            request.setAttribute("submissionStatsMap", submissionDAO.getSubmissionStatsMap(currentUser.getUserId()));

            request.getRequestDispatcher("/instructor/assignments.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"instructor".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        AssignmentDAO assignmentDAO = new AssignmentDAO();

        // Feature 11: Handle duplicate separately (different form fields)
        if ("duplicate".equals(action)) {
            try {
                int srcId = Integer.parseInt(request.getParameter("sourceId"));
                int targetCourseId = Integer.parseInt(request.getParameter("targetCourseId"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date newDeadline = sdf.parse(request.getParameter("newDeadline"));
                String softStr = request.getParameter("newSoftDeadline");
                String hardStr = request.getParameter("newHardDeadline");
                Date newSoftDeadline = (softStr != null && !softStr.isEmpty()) ? sdf.parse(softStr) : null;
                Date newHardDeadline = (hardStr != null && !hardStr.isEmpty()) ? sdf.parse(hardStr) : null;

                Assignment src = assignmentDAO.getAssignmentById(srcId);
                if (src != null && src.getInstructorId() == currentUser.getUserId()) {
                    int newId = assignmentDAO.duplicateAssignment(srcId, targetCourseId, currentUser.getUserId(),
                        newDeadline, newSoftDeadline, newHardDeadline);
                    if (newId > 0) {
                        new AuditLogDAO().logAction(currentUser.getUserId(), "DUPLICATE_ASSIGNMENT", newId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/assignments?duplicated=true");
            return;
        }
        try {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String deadlineStr = request.getParameter("deadline");
            String softDeadlineStr = request.getParameter("softDeadline");
            String hardDeadlineStr = request.getParameter("hardDeadline");
            int courseId = Integer.parseInt(request.getParameter("courseId"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date deadline = sdf.parse(deadlineStr);
            Date softDeadline = (softDeadlineStr != null && !softDeadlineStr.isEmpty()) ? sdf.parse(softDeadlineStr) : null;
            Date hardDeadline = (hardDeadlineStr != null && !hardDeadlineStr.isEmpty()) ? sdf.parse(hardDeadlineStr) : null;

            // Handle file upload
            String attachmentPath = null;
            Part filePart = request.getPart("attachment");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = System.currentTimeMillis() + "_" + getFileName(filePart);
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                filePart.write(UPLOAD_DIR + fileName);
                attachmentPath = fileName;
            }

            if ("edit".equals(action)) {
                int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
                Assignment existingAssignment = assignmentDAO.getAssignmentById(assignmentId);
                if (existingAssignment != null && existingAssignment.getInstructorId() == currentUser.getUserId()) {
                    existingAssignment.setTitle(title);
                    existingAssignment.setDescription(description);
                    existingAssignment.setDeadline(deadline);
                    existingAssignment.setSoftDeadline(softDeadline);
                    existingAssignment.setHardDeadline(hardDeadline);
                    existingAssignment.setCourseId(courseId);
                    if (attachmentPath != null) {
                        existingAssignment.setAttachmentPath(attachmentPath);
                    }
                    assignmentDAO.updateAssignment(existingAssignment);
                }
            } else {
                Assignment assignment = new Assignment();
                assignment.setCourseId(courseId);
                assignment.setInstructorId(currentUser.getUserId());
                assignment.setTitle(title);
                assignment.setDescription(description);
                assignment.setDeadline(deadline);
                assignment.setSoftDeadline(softDeadline);
                assignment.setHardDeadline(hardDeadline);
                assignment.setAttachmentPath(attachmentPath);
                assignmentDAO.createAssignment(assignment);

                // Notify enrolled students about new assignment
                NotificationDAO notifDao = new NotificationDAO();
                notifDao.notifyStudentsInCourse(courseId, "New Assignment: " + title,
                    "A new assignment '" + title + "' has been posted. Deadline: " + deadlineStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to save assignment: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/assignments");
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
