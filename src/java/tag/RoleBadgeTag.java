package tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Custom JSP Tag that renders a Bootstrap badge for a user role.
 * Usage: <ct:roleBadge role="${user.role}" />
 */
public class RoleBadgeTag extends SimpleTagSupport {

    private String role;

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        String badgeClass;
        String displayRole;

        if (role == null) {
            role = "unknown";
        }

        switch (role.toLowerCase()) {
            case "admin":
                badgeClass = "bg-danger";
                displayRole = "Admin";
                break;
            case "instructor":
                badgeClass = "bg-primary";
                displayRole = "Instructor";
                break;
            case "student":
                badgeClass = "bg-success";
                displayRole = "Student";
                break;
            default:
                badgeClass = "bg-secondary";
                displayRole = role;
                break;
        }

        out.print("<span class=\"badge " + badgeClass + "\">" + displayRole + "</span>");
    }
}
