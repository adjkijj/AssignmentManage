package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LogoutServlet - Handles user logout by invalidating the session.
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.User user = (model.User) session.getAttribute("currentUser");
            if (user != null) {
                new service.AuditLogService().log(request, user.getUserId(), user.getUsername(), "LOGOUT", "User", user.getUserId(), "Logged out.");
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
