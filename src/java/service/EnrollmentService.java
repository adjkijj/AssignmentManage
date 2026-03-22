package service;

import dao.EnrollmentDAO;
import model.Enrollment;
import java.util.List;

/**
 * Service for Advanced Enrollment Search and Pagination.
 */
public class EnrollmentService {
    
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    
    public List<Enrollment> search(String keyword, String courseIdStr, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return enrollmentDAO.searchEnrollments(keyword, courseIdStr, offset, pageSize);
    }
    
    public int getTotalPages(String keyword, String courseIdStr, int pageSize) {
        int totalRecords = enrollmentDAO.countSearchEnrollments(keyword, courseIdStr);
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
    
    public int getTotalRecords(String keyword, String courseIdStr) {
        return enrollmentDAO.countSearchEnrollments(keyword, courseIdStr);
    }
}
