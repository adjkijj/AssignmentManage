package controller;

import dao.PasswordResetTokenDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.PasswordUtil;
import java.io.IOException;

/**
 * ResetPasswordServlet - Allows user to set a new password using a valid token.
 * GET: show new password form with token
 * POST: validate token, hash new password, save
 */
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Invalid or missing reset token.");
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        PasswordResetTokenDAO tokenDAO = new PasswordResetTokenDAO();
        int userId = tokenDAO.findValidToken(token);
        if (userId == -1) {
            request.setAttribute("error", "This reset link is invalid or has expired.");
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        request.setAttribute("token", token);
        request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || newPassword == null || newPassword.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
            return;
        }

        PasswordResetTokenDAO tokenDAO = new PasswordResetTokenDAO();
        int userId = tokenDAO.findValidToken(token);

        if (userId == -1) {
            request.setAttribute("error", "This reset link is invalid or has expired.");
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        // Hash new password and update
        String hashed = PasswordUtil.hashPassword(newPassword);
        UserDAO userDAO = new UserDAO();
        if (userDAO.updatePassword(userId, hashed)) {
            tokenDAO.markUsed(token);
            request.setAttribute("success", "Password reset successful! Please login with your new password.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to reset password. Please try again.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/auth/reset-password.jsp").forward(request, response);
        }
    }
}
