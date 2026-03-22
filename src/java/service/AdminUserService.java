package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

public class AdminUserService {

    private UserDAO userDAO = new UserDAO();

    public boolean updateUserInfo(int userId, String fullName, String email, String role, StringBuilder errorMsg) {
        User existing = userDAO.getUserById(userId);
        if (existing == null) {
            errorMsg.append("User not found.");
            return false;
        }

        // Check duplicate email if it changed
        if (!existing.getEmail().equalsIgnoreCase(email)) {
            if (userDAO.emailExists(email)) {
                errorMsg.append("Email is already in use by another account.");
                return false;
            }
        }

        existing.setFullName(fullName);
        existing.setEmail(email);
        existing.setRole(role);

        return userDAO.updateUser(existing);
    }

    public boolean resetPassword(int userId, String newPassword, StringBuilder errorMsg) {
        if (newPassword == null || newPassword.length() < 6) {
            errorMsg.append("Password must be at least 6 characters.");
            return false;
        }

        User existing = userDAO.getUserById(userId);
        if (existing == null) {
            errorMsg.append("User not found.");
            return false;
        }

        // Use existing PasswordUtil (PBKDF2) as acceptable strong hash alternative.
        String hashed = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashed);
    }
}
