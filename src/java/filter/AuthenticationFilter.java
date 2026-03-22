package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication Filter - checks if user is logged in.
 * Allows access to login, register, and static resources without authentication.
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Allow access to public resources without authentication
        if (uri.equals(contextPath + "/login") ||
            uri.equals(contextPath + "/register") ||
            uri.equals(contextPath + "/") ||
            uri.startsWith(contextPath + "/css/") ||
            uri.startsWith(contextPath + "/js/") ||
            uri.startsWith(contextPath + "/images/") ||
            uri.endsWith(".css") ||
            uri.endsWith(".js") ||
            uri.endsWith(".png") ||
            uri.endsWith(".jpg") ||
            uri.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {}
}
