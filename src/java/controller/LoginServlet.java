package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;

/**
 * LoginServlet - Handles user authentication.
 * GET: Display login form
 * POST: Process login credentials
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username.trim(), password.trim());

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            
            new service.AuditLogService().log(request, user.getUserId(), user.getUsername(), "LOGIN", "User", user.getUserId(), "Logged in successfully.");
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Invalid username or password, or account is deactivated.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }
}
