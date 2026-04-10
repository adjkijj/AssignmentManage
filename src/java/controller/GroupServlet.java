package controller;

import dao.AssignmentDAO;
import dao.EnrollmentDAO;
import dao.GroupDAO;
import dao.SubmissionDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Assignment;
import model.Group;
import model.Submission;
import model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GroupServlet - Manages group assignments.
 * Student actions: myGroups (view groups the student belongs to)
 * Instructor actions: manage, create, addMember, removeMember, gradeMembers
 */
public class GroupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");
        if (action == null) action = "manage";

        // ========== Student: View My Groups ==========
        if ("student".equals(currentUser.getRole()) && "myGroups".equals(action)) {
            GroupDAO groupDAO = new GroupDAO();
            AssignmentDAO assignmentDAO = new AssignmentDAO();
            List<Group> myGroups = groupDAO.getGroupsForStudent(currentUser.getUserId());

            // Build a map of assignmentId -> assignment title for display
            // And a map of groupId -> student's own submission (for grades)
            Map<Integer, String> assignmentTitles = new HashMap<>();
            Map<Integer, Submission> myGrades = new HashMap<>();

            for (Group g : myGroups) {
                if (!assignmentTitles.containsKey(g.getAssignmentId())) {
                    Assignment a = assignmentDAO.getAssignmentById(g.getAssignmentId());
                    if (a != null) {
                        assignmentTitles.put(g.getAssignmentId(), a.getTitle());
                    }
                }
                
                // Get the student's OWN submission for this group's assignment
                Submission sub = groupDAO.getLatestSubmission(currentUser.getUserId(), g.getAssignmentId());
                if (sub != null) {
                    myGrades.put(g.getGroupId(), sub);
                }
            }
            request.setAttribute("myGroups", myGroups);
            request.setAttribute("assignmentTitles", assignmentTitles);
            request.setAttribute("myGrades", myGrades);
            request.getRequestDispatcher("/student/my-groups.jsp").forward(request, response);
            return;
        }

        // ========== Instructor only from here ==========
        if (!"instructor".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // ========== Instructor: Grade Members of a Group ==========
        if ("gradeMembers".equals(action)) {
            int groupId = Integer.parseInt(request.getParameter("groupId"));
            GroupDAO groupDAO = new GroupDAO();
            Group group = groupDAO.getGroupById(groupId);
            if (group == null) {
                response.sendRedirect(request.getContextPath() + "/groups");
                return;
            }
            AssignmentDAO assignmentDAO = new AssignmentDAO();
            Assignment assignment = assignmentDAO.getAssignmentById(group.getAssignmentId());

            // Build submission map: studentId -> latest Submission
            Map<Integer, Submission> memberSubmissions = new HashMap<>();
            if (group.getMembers() != null) {
                for (User member : group.getMembers()) {
                    Submission sub = groupDAO.getLatestSubmission(member.getUserId(), group.getAssignmentId());
                    if (sub != null) {
                        memberSubmissions.put(member.getUserId(), sub);
                    }
                }
            }

            request.setAttribute("group", group);
            request.setAttribute("assignment", assignment);
            request.setAttribute("memberSubmissions", memberSubmissions);
            request.getRequestDispatcher("/instructor/grade-group-members.jsp").forward(request, response);
            return;
        }

        // ========== Instructor: Manage Groups (existing logic) ==========
        int assignmentId = 0;
        String assignmentIdStr = request.getParameter("assignmentId");
        if (assignmentIdStr != null && !assignmentIdStr.isEmpty()) {
            assignmentId = Integer.parseInt(assignmentIdStr);
        }

        GroupDAO groupDAO = new GroupDAO();
        AssignmentDAO assignmentDAO = new AssignmentDAO();

        if (assignmentId > 0) {
            Assignment assignment = assignmentDAO.getAssignmentById(assignmentId);
            List<Group> groups = groupDAO.getGroupsByAssignment(assignmentId);

            // Load members for each group
            for (Group g : groups) {
                g.setMembers(groupDAO.getGroupMembers(g.getGroupId()));
            }

            // Collect all student IDs already assigned to any group for this assignment
            Set<Integer> assignedStudentIds = new HashSet<>();
            for (Group g : groups) {
                if (g.getMembers() != null) {
                    for (User member : g.getMembers()) {
                        assignedStudentIds.add(member.getUserId());
                    }
                }
            }
            request.setAttribute("assignedStudentIds", assignedStudentIds);

            // Get enrolled students for adding to groups
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
            request.setAttribute("enrolledStudents",
                enrollmentDAO.getEnrollmentsByCourse(assignment.getCourseId()));

            request.setAttribute("assignment", assignment);
            request.setAttribute("groups", groups);
        } else {
            // Show all assignments for instructor to pick
            request.setAttribute("assignments",
                assignmentDAO.getAssignmentsByInstructor(currentUser.getUserId()));
        }

        request.getRequestDispatcher("/instructor/groups.jsp").forward(request, response);
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
        GroupDAO groupDAO = new GroupDAO();

        // ========== Instructor: Grade individual group members ==========
        if ("gradeGroupMembers".equals(action)) {
            int groupId = Integer.parseInt(request.getParameter("groupId"));
            Group group = groupDAO.getGroupById(groupId);
            if (group == null) {
                response.sendRedirect(request.getContextPath() + "/groups");
                return;
            }

            SubmissionDAO submissionDAO = new SubmissionDAO();
            int gradedCount = 0;

            if (group.getMembers() != null) {
                for (User member : group.getMembers()) {
                    String gradeStr = request.getParameter("grade_" + member.getUserId());
                    String feedback = request.getParameter("feedback_" + member.getUserId());

                    if (gradeStr != null && !gradeStr.trim().isEmpty()) {
                        double grade = Double.parseDouble(gradeStr);
                        // Get or find the submission for this student
                        Submission sub = groupDAO.getLatestSubmission(member.getUserId(), group.getAssignmentId());
                        
                        if (sub != null) {
                            // Update existing submission
                            submissionDAO.gradeSubmission(sub.getSubmissionId(), grade, feedback);
                            gradedCount++;
                        } else {
                            // Create a new "empty" submission because they didn't submit natively
                            Submission newSub = new Submission();
                            newSub.setAssignmentId(group.getAssignmentId());
                            newSub.setStudentId(member.getUserId());
                            newSub.setGroupId(group.getGroupId());
                            newSub.setVersion(1);
                            newSub.setFilePath("Nộp theo nhóm (Giáo viên chấm chung)");
                            newSub.setStatus("graded");
                            newSub.setIsLate(false);
                            
                            // Insert it
                            if (submissionDAO.submitAssignment(newSub)) {
                                // Retrieve it to apply the actual grade (submitAssignment doesn't set grade directly)
                                Submission createdSub = groupDAO.getLatestSubmission(member.getUserId(), group.getAssignmentId());
                                if (createdSub != null) {
                                    submissionDAO.gradeSubmission(createdSub.getSubmissionId(), grade, feedback);
                                    gradedCount++;
                                }
                            }
                        }
                    }
                }
            }

            request.getSession().setAttribute("success",
                "Đã chấm điểm " + gradedCount + " thành viên trong nhóm.");
            response.sendRedirect(request.getContextPath() + "/groups?action=gradeMembers&groupId=" + groupId);
            return;
        }

        // ========== Existing instructor actions ==========
        int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));

        switch (action) {
            case "create":
                String groupName = request.getParameter("groupName");
                Group newGroup = new Group();
                newGroup.setAssignmentId(assignmentId);
                newGroup.setGroupName(groupName);
                if (groupDAO.createGroup(newGroup) > 0) {
                    request.getSession().setAttribute("success", "Group '" + groupName + "' created.");
                } else {
                    request.getSession().setAttribute("error", "Failed to create group.");
                }
                break;

            case "addMember":
            case "addMembers":
                int groupId = Integer.parseInt(request.getParameter("groupId"));
                String[] studentIds = request.getParameterValues("studentId");
                if (studentIds != null && studentIds.length > 0) {
                    int added = 0;
                    for (String sid : studentIds) {
                        if (groupDAO.addMemberToGroup(groupId, Integer.parseInt(sid))) {
                            added++;
                        }
                    }
                    request.getSession().setAttribute("success", added + " student(s) added to group.");
                } else {
                    request.getSession().setAttribute("error", "No students selected.");
                }
                break;

            case "removeMember":
                int rmGroupId = Integer.parseInt(request.getParameter("groupId"));
                int rmStudentId = Integer.parseInt(request.getParameter("studentId"));
                if (groupDAO.removeMemberFromGroup(rmGroupId, rmStudentId)) {
                    request.getSession().setAttribute("success", "Student removed from group.");
                } else {
                    request.getSession().setAttribute("error", "Failed to remove student.");
                }
                break;
        }

        response.sendRedirect(request.getContextPath() + "/groups?assignmentId=" + assignmentId);
    }
}
