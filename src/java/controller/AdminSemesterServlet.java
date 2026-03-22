package controller;

import dao.SemesterDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Semester;
import model.User;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * AdminSemesterServlet - CRUD for Semesters (admin only).
 */
public class AdminSemesterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SemesterDAO dao = new SemesterDAO();
        request.setAttribute("semesters", dao.getAllSemesters());
        request.getRequestDispatcher("/admin/semesters.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        SemesterDAO dao = new SemesterDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            switch (action) {
                case "create": {
                    Semester s = new Semester();
                    s.setSemesterName(request.getParameter("semesterName"));
                    s.setStartDate(sdf.parse(request.getParameter("startDate")));
                    s.setEndDate(sdf.parse(request.getParameter("endDate")));
                    if (dao.createSemester(s)) {
                        request.getSession().setAttribute("success", "Semester created.");
                    } else {
                        request.getSession().setAttribute("error", "Failed to create semester.");
                    }
                    break;
                }
                case "update": {
                    Semester s = new Semester();
                    s.setSemesterId(Integer.parseInt(request.getParameter("semesterId")));
                    s.setSemesterName(request.getParameter("semesterName"));
                    s.setStartDate(sdf.parse(request.getParameter("startDate")));
                    s.setEndDate(sdf.parse(request.getParameter("endDate")));
                    if (dao.updateSemester(s)) {
                        request.getSession().setAttribute("success", "Semester updated.");
                    } else {
                        request.getSession().setAttribute("error", "Failed to update semester.");
                    }
                    break;
                }
                case "delete": {
                    try {
                        dao.deleteSemester(Integer.parseInt(request.getParameter("semesterId")));
                        request.getSession().setAttribute("success", "Semester deleted successfully.");
                    } catch (Exception e) {
                        request.getSession().setAttribute("error", "Cannot delete this semester because it contains active classes/courses.");
                    }
                    break;
                }
                case "toggle":
                    dao.toggleActive(Integer.parseInt(request.getParameter("semesterId")));
                    request.getSession().setAttribute("success", "Semester status toggled.");
                    break;
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/semesters");
    }
}
