package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import java.util.List;

/**
 * AdminUserServlet - Handles user management for administrators.
 * Actions: list, create, edit, toggleActive
 */
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        UserDAO userDAO = new UserDAO();

        switch (action) {
            case "toggleActive":
                int userId = Integer.parseInt(request.getParameter("id"));
                userDAO.toggleActive(userId);
                
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_USER", "User", userId, "Toggled active status");
                
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;

            case "edit":
                int editId = Integer.parseInt(request.getParameter("id"));
                User editUser = userDAO.getUserById(editId);
                request.setAttribute("editUser", editUser);
                break;
        }

        String keyword = request.getParameter("keyword");
        String roleFilter = request.getParameter("roleFilter");
        String statusFilter = request.getParameter("statusFilter");
        
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (NumberFormatException e) { page = 1; }
        }
        
        service.UserSearchService searchService = new service.UserSearchService();
        List<User> users = searchService.search(keyword, roleFilter, statusFilter, page, pageSize);
        int totalPages = searchService.getTotalPages(keyword, roleFilter, statusFilter, pageSize);
        int totalRecords = searchService.getTotalRecords(keyword, roleFilter, statusFilter);
        
        request.setAttribute("users", users);
        request.setAttribute("keyword", keyword);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", pageSize);
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        if ("create".equals(action)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String role = request.getParameter("role");

            if (userDAO.usernameExists(username)) {
                request.setAttribute("error", "Username already exists.");
            } else if (userDAO.emailExists(email)) {
                request.setAttribute("error", "Email already exists.");
            } else {
                User user = new User();
                user.setUsername(username.trim());
                user.setPassword(password.trim());
                user.setFullName(fullName.trim());
                user.setEmail(email.trim());
                user.setRole(role);
                if (userDAO.register(user)) {
                    model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                    new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "CREATE_USER", "User", null, "Created user: " + username.trim());
                    request.setAttribute("success", "User created successfully.");
                } else {
                    request.setAttribute("error", "Failed to create user.");
                }
            }
        } else if ("edit".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String role = request.getParameter("role");

            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setRole(role);
            if (userDAO.updateUser(user)) {
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "UPDATE_USER", "User", userId, "Updated user: " + email.trim());
                request.setAttribute("success", "User updated successfully.");
            } else {
                request.setAttribute("error", "Failed to update user.");
            }
        } else if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            try {
                userDAO.deleteUser(userId);
                model.User currentUser = (model.User) request.getSession().getAttribute("currentUser");
                new service.AuditLogService().log(request, currentUser.getUserId(), currentUser.getUsername(), "DELETE_USER", "User", userId, "Deleted user");
                request.setAttribute("success", "User deleted successfully.");
            } catch (Exception e) {
                request.setAttribute("error", "Cannot delete this user because they have submitted assignments, grades, enrollments, or groups. Please lock their account instead.");
            }
        }

        service.UserSearchService searchService = new service.UserSearchService();
        request.setAttribute("users", searchService.search(null, null, null, 1, 10));
        request.setAttribute("currentPage", 1);
        request.setAttribute("totalPages", searchService.getTotalPages(null, null, null, 10));
        request.setAttribute("totalRecords", searchService.getTotalRecords(null, null, null));
        request.setAttribute("pageSize", 10);
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
}
