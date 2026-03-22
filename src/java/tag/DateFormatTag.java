package tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom JSP Tag that formats a Date object into a readable string.
 * Usage: <ct:dateFormat date="${someDate}" pattern="dd/MM/yyyy HH:mm" />
 */
public class DateFormatTag extends SimpleTagSupport {

    private Date date;
    private String pattern = "dd/MM/yyyy HH:mm";

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                out.print(sdf.format(date));
            } catch (Exception e) {
                out.print(date.toString());
            }
        } else {
            out.print("N/A");
        }
    }
}
