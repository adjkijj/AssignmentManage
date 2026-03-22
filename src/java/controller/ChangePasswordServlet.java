package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.PasswordUtil;
import java.io.IOException;

/**
 * ChangePasswordServlet - Allows logged-in users to change their password.
 * GET: show change password form
 * POST: verify old password, hash and save new password
 */
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("error", "New password must be at least 6 characters.");
            request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match.");
            request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
            return;
        }

        // Verify old password
        UserDAO userDAO = new UserDAO();
        User freshUser = userDAO.getUserById(currentUser.getUserId());
        if (!PasswordUtil.verifyPassword(oldPassword, freshUser.getPassword())) {
            request.setAttribute("error", "Current password is incorrect.");
            request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
            return;
        }

        // Hash and save new password
        String hashed = PasswordUtil.hashPassword(newPassword);
        if (userDAO.updatePassword(currentUser.getUserId(), hashed)) {
            request.setAttribute("success", "Password changed successfully!");
        } else {
            request.setAttribute("error", "Failed to change password. Please try again.");
        }
        request.getRequestDispatcher("/common/change-password.jsp").forward(request, response);
    }
}
