package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ImportUsersServlet - Admin CSV bulk import of users.
 * CSV format: username,password,fullName,email,role
 */
@MultipartConfig
public class ImportUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.getRequestDispatcher("/admin/import-users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (!"admin".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Part filePart = request.getPart("csvFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "Please select a CSV file to upload.");
            request.getRequestDispatcher("/admin/import-users.jsp").forward(request, response);
            return;
        }

        List<User> newUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int lineNum = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;

                // Skip header row if present
                if (lineNum == 1 && line.toLowerCase().contains("username")) continue;

                String[] data = line.split(",");
                if (data.length >= 5) {
                    User u = new User();
                    u.setUsername(data[0].trim());
                    u.setPassword(data[1].trim());
                    u.setFullName(data[2].trim());
                    u.setEmail(data[3].trim());
                    u.setRole(data[4].trim());
                    newUsers.add(u);
                } else {
                    errors.add("Line " + lineNum + ": Insufficient columns (expected 5, got " + data.length + ")");
                }
            }
        }

        if (newUsers.isEmpty()) {
            request.setAttribute("error", "No valid users found in the CSV file.");
            request.setAttribute("parseErrors", errors);
            request.getRequestDispatcher("/admin/import-users.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        int inserted = userDAO.insertUsersBatch(newUsers);

        request.setAttribute("success", "Successfully imported " + inserted + " out of " + newUsers.size() + " users.");
        if (!errors.isEmpty()) {
            request.setAttribute("parseErrors", errors);
        }
        request.getRequestDispatcher("/admin/import-users.jsp").forward(request, response);
    }
}
