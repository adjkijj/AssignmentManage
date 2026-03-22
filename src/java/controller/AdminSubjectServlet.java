package controller;

import dao.SubjectDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Subject;
import java.io.IOException;

/**
 * AdminSubjectServlet - CRUD for Subjects (admin only).
 */
public class AdminSubjectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SubjectDAO dao = new SubjectDAO();
        request.setAttribute("subjects", dao.getAllSubjects());
        request.getRequestDispatcher("/admin/subjects.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        SubjectDAO dao = new SubjectDAO();

        switch (action) {
            case "create": {
                Subject s = new Subject();
                s.setSubjectCode(request.getParameter("subjectCode"));
                s.setSubjectName(request.getParameter("subjectName"));
                s.setDescription(request.getParameter("description"));
                if (dao.createSubject(s)) {
                    request.getSession().setAttribute("success", "Subject created.");
                } else {
                    request.getSession().setAttribute("error", "Failed to create subject.");
                }
                break;
            }
            case "update": {
                Subject s = new Subject();
                s.setSubjectId(Integer.parseInt(request.getParameter("subjectId")));
                s.setSubjectCode(request.getParameter("subjectCode"));
                s.setSubjectName(request.getParameter("subjectName"));
                s.setDescription(request.getParameter("description"));
                if (dao.updateSubject(s)) {
                    request.getSession().setAttribute("success", "Subject updated.");
                } else {
                    request.getSession().setAttribute("error", "Failed to update subject.");
                }
                break;
            }
            case "delete": {
                try {
                    dao.deleteSubject(Integer.parseInt(request.getParameter("subjectId")));
                    request.getSession().setAttribute("success", "Subject deleted successfully.");
                } catch (Exception e) {
                    request.getSession().setAttribute("error", "Cannot delete this subject because it is assigned to existing courses.");
                }
                break;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/subjects");
    }
}
