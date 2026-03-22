package service;

import dao.UserDAO;
import model.User;
import java.util.List;

/**
 * Service for Advanced User Search and Pagination.
 */
public class UserSearchService {
    
    private UserDAO userDAO = new UserDAO();
    
    public List<User> search(String keyword, String role, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userDAO.searchUsers(keyword, role, status, offset, pageSize);
    }
    
    public int getTotalPages(String keyword, String role, String status, int pageSize) {
        int totalRecords = userDAO.countSearchUsers(keyword, role, status);
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
    
    public int getTotalRecords(String keyword, String role, String status) {
        return userDAO.countSearchUsers(keyword, role, status);
    }
}
