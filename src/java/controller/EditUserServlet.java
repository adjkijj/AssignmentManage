package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import dao.UserDAO;
import service.AdminUserService;
import service.AuditLogService;

import java.io.IOException;

public class EditUserServlet extends HttpServlet {

    private AdminUserService adminUserService = new AdminUserService();
    private UserDAO userDAO = new UserDAO();
    private AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            User userToEdit = userDAO.getUserById(userId);
            if (userToEdit == null) {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/admin/users").forward(request, response);
                return;
            }
            request.setAttribute("editUser", userToEdit);
            request.getRequestDispatcher("/admin/edit-user.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        int targetUserId = Integer.parseInt(request.getParameter("userId"));
        StringBuilder errorMsg = new StringBuilder();

        User userToEdit = userDAO.getUserById(targetUserId);

        if ("updateInfo".equals(action)) {
            String fullName = request.getParameter("fullName").trim();
            String email = request.getParameter("email").trim();
            String role = request.getParameter("role").trim();

            if (adminUserService.updateUserInfo(targetUserId, fullName, email, role, errorMsg)) {
                auditLogService.log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_USER", "User", targetUserId, "Updated user info for " + userToEdit.getUsername());
                request.getSession().setAttribute("flashSuccess", "User information updated successfully.");
                response.sendRedirect(request.getContextPath() + "/admin/edit-user?id=" + targetUserId);
                return;
            } else {
                request.setAttribute("error", errorMsg.toString());
            }
        } else if ("resetPassword".equals(action)) {
            String newPassword = request.getParameter("newPassword");
            if (adminUserService.resetPassword(targetUserId, newPassword, errorMsg)) {
                auditLogService.log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_USER", "User", targetUserId, "Reset password for " + userToEdit.getUsername());
                request.getSession().setAttribute("flashSuccess", "Password reset successfully for user " + userToEdit.getUsername());
                response.sendRedirect(request.getContextPath() + "/admin/edit-user?id=" + targetUserId);
                return;
            } else {
                request.setAttribute("error", errorMsg.toString());
            }
        }

        // If error occurred, reload page with user data
        request.setAttribute("editUser", userDAO.getUserById(targetUserId));
        request.getRequestDispatcher("/admin/edit-user.jsp").forward(request, response);
    }
}
