package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;

/**
 * Authorization Filter - checks role-based permissions.
 * Restricts access to admin and instructor-specific URLs.
 */
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Skip authorization for public resources
        if (uri.equals(contextPath + "/login") ||
            uri.equals(contextPath + "/register") ||
            uri.equals(contextPath + "/") ||
            uri.startsWith(contextPath + "/css/") ||
            uri.endsWith(".css") || uri.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            chain.doFilter(request, response);
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String role = currentUser.getRole();

        // Admin-only URLs
        if (uri.startsWith(contextPath + "/admin/")) {
            if (!"admin".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Admin privileges required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
