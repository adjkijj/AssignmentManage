package controller;

import dao.AssignmentDAO;
import dao.EnrollmentDAO;
import dao.GroupDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Assignment;
import model.Group;
import model.User;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GroupServlet - Manages group assignments for instructors.
 * Actions: manage (view groups for assignment), create, addMember, removeMember
 */
public class GroupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"instructor".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "manage";

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
