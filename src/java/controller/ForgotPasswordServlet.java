package controller;

import dao.PasswordResetTokenDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * ForgotPasswordServlet - Generates a password reset token.
 * GET: show forgot password form
 * POST: generate token and display reset link
 */
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Please enter your email address.");
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email.trim());

        if (user == null) {
            request.setAttribute("error", "No active account found with that email.");
            request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
            return;
        }

        // Generate token (valid for 30 minutes)
        String token = UUID.randomUUID().toString();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        Date expiresAt = cal.getTime();

        PasswordResetTokenDAO tokenDAO = new PasswordResetTokenDAO();
        tokenDAO.createToken(user.getUserId(), token, expiresAt);

        // Show reset link on screen (demo mode - no email server)
        String resetLink = request.getContextPath() + "/reset-password?token=" + token;
        request.setAttribute("resetLink", resetLink);
        request.setAttribute("success", "Password reset link generated! Use the link below (valid for 30 minutes):");
        request.getRequestDispatcher("/auth/forgot-password.jsp").forward(request, response);
    }
}
