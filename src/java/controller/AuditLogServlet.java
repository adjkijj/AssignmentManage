package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.User;
import service.AuditLogService;

import java.io.IOException;
import java.util.List;

public class AuditLogServlet extends HttpServlet {

    private AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Ensure Admin role
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Read filter params
        String actionFilter = request.getParameter("actionFilter");
        String usernameFilter = request.getParameter("usernameFilter");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        
        // Read pagination state
        int page = 1;
        int pageSize = 20; // Default page size per instructions
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Fetch data
        List<AuditLog> logs = auditLogService.getLogsPagedFiltered(actionFilter, usernameFilter, dateFrom, dateTo, page, pageSize);
        int totalPages = auditLogService.getTotalPages(actionFilter, usernameFilter, dateFrom, dateTo, pageSize);
        int totalRecords = auditLogService.getTotalRecords(actionFilter, usernameFilter, dateFrom, dateTo);

        // Keep filters in request scope for UI rendering
        request.setAttribute("actionFilter", actionFilter);
        request.setAttribute("usernameFilter", usernameFilter);
        request.setAttribute("dateFrom", dateFrom);
        request.setAttribute("dateTo", dateTo);
        
        request.setAttribute("logs", logs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/admin/audit-log.jsp").forward(request, response);
    }
}
