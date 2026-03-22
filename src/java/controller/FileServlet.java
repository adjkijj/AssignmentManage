package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * FileServlet - Serves uploaded files from the C:/uploads/ directory.
 * Maps /uploads/* URL paths to physical files on disk.
 * Prevents path traversal attacks by validating the resolved path.
 */
public class FileServlet extends HttpServlet {

    private static final String BASE_UPLOAD_DIR = "C:/uploads/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the path after /uploads/
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file specified.");
            return;
        }

        // Security: prevent path traversal
        File file = new File(BASE_UPLOAD_DIR, pathInfo).getCanonicalFile();
        if (!file.getAbsolutePath().startsWith(new File(BASE_UPLOAD_DIR).getCanonicalPath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found.");
            return;
        }

        // Set content type
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());

        // Set Content-Disposition for download (inline for viewable types, attachment for others)
        String disposition = "inline";
        if ("application/octet-stream".equals(contentType)) {
            disposition = "attachment";
        }
        response.setHeader("Content-Disposition", disposition + "; filename=\"" + file.getName() + "\"");

        // Stream the file
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
