package controller;

import dao.NotificationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;

/**
 * NotificationServlet - Handles notifications display and mark-as-read.
 * Actions: list (default), markRead, markAllRead
 */
public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String action = request.getParameter("action");
        if (action == null) action = "list";

        NotificationDAO notificationDAO = new NotificationDAO();

        switch (action) {
            case "markRead":
                int notifId = Integer.parseInt(request.getParameter("id"));
                notificationDAO.markAsRead(notifId);
                response.sendRedirect(request.getContextPath() + "/notifications");
                return;

            case "markAllRead":
                notificationDAO.markAllAsRead(currentUser.getUserId());
                response.sendRedirect(request.getContextPath() + "/notifications");
                return;

            case "delete":
                try {
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    notificationDAO.deleteNotification(deleteId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/notifications");
                return;

            case "list":
            default:
                request.setAttribute("notifications",
                    notificationDAO.getRecentByUser(currentUser.getUserId(), 50));
                request.setAttribute("unreadCount",
                    notificationDAO.countUnread(currentUser.getUserId()));
                request.getRequestDispatcher("/common/notifications.jsp").forward(request, response);
                return;
        }
    }
}
