package controller;

import dao.CourseMaterialDAO;
import dao.CourseDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.CourseMaterial;
import model.User;
import util.FileValidationUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * CourseMaterialServlet - Upload/manage course materials.
 * GET  ?courseId=X            → student view (list visible materials)
 * GET  ?action=manage&courseId=X → instructor management view
 * POST ?action=upload        → handle file upload
 * POST ?action=delete&id=X   → delete material
 * POST ?action=toggle&id=X   → toggle visibility
 */
public class CourseMaterialServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/uploads/materials/";
    private static final Logger logger = Logger.getLogger(CourseMaterialServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String courseIdStr = request.getParameter("courseId");
        if (courseIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        int courseId = Integer.parseInt(courseIdStr);

        CourseMaterialDAO dao = new CourseMaterialDAO();
        CourseDAO courseDAO = new CourseDAO();
        request.setAttribute("course", courseDAO.getCourseById(courseId));

        if ("manage".equals(action) && ("instructor".equals(currentUser.getRole()) || "admin".equals(currentUser.getRole()))) {
            request.setAttribute("materials", dao.getByCourseAll(courseId));
            request.getRequestDispatcher("/instructor/materials-manage.jsp").forward(request, response);
        } else {
            request.setAttribute("materials", dao.getByCourse(courseId));
            request.getRequestDispatcher("/student/materials.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || (!"instructor".equals(currentUser.getRole()) && !"admin".equals(currentUser.getRole()))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        CourseMaterialDAO dao = new CourseMaterialDAO();

        if ("upload".equals(action)) {
            handleUpload(request, response, currentUser, dao);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            dao.delete(id);
            response.sendRedirect(request.getContextPath() + "/course-materials?action=manage&courseId=" + courseId);
        } else if ("toggle".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            dao.toggleVisibility(id);
            response.sendRedirect(request.getContextPath() + "/course-materials?action=manage&courseId=" + courseId);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleUpload(HttpServletRequest request, HttpServletResponse response,
                              User currentUser, CourseMaterialDAO dao)
            throws ServletException, IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));

        CourseMaterial m = new CourseMaterial();
        m.setCourseId(courseId);
        m.setTitle(request.getParameter("title"));
        m.setDescription(request.getParameter("description"));
        m.setExternalUrl(request.getParameter("externalUrl"));
        m.setMaterialType(request.getParameter("materialType"));
        m.setTopic(request.getParameter("topic"));
        m.setUploadedBy(currentUser.getUserId());

        String weekStr = request.getParameter("weekNumber");
        if (weekStr != null && !weekStr.isEmpty()) m.setWeekNumber(Integer.parseInt(weekStr));

        // Handle file upload (if provided)
        Part filePart = request.getPart("file");
        if (filePart != null && filePart.getSize() > 0) {
            String originalName = getFileName(filePart);

            // Validate file using Feature 2 logic
            String valError = FileValidationUtil.validate(originalName, filePart.getInputStream());
            if (valError != null) {
                logger.warning("[BLOCKED MATERIAL UPLOAD] user=" + currentUser.getUserId()
                             + " file=" + originalName);
                request.getSession().setAttribute("error", valError);
                response.sendRedirect(request.getContextPath()
                    + "/course-materials?action=manage&courseId=" + courseId);
                return;
            }

            String fileName = System.currentTimeMillis() + "_" + originalName;
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            filePart.write(UPLOAD_DIR + fileName);
            m.setFilePath(fileName);
        }

        if (dao.create(m)) {
            request.getSession().setAttribute("success", "Material uploaded successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to upload material.");
        }
        response.sendRedirect(request.getContextPath()
            + "/course-materials?action=manage&courseId=" + courseId);
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "unknown";
    }
}
