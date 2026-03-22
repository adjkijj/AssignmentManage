package service;

import dao.CourseDAO;
import dao.CourseDetailDAO;
import model.CourseDetail;

public class CourseDetailService {
    
    private CourseDAO courseDAO = new CourseDAO();
    private CourseDetailDAO detailDAO = new CourseDetailDAO();

    public CourseDetail getCourseDetail(int courseId) {
        CourseDetail detail = new CourseDetail();
        detail.setCourse(courseDAO.getCourseById(courseId));
        if (detail.getCourse() != null) {
            detail.setEnrolledStudents(detailDAO.getEnrolledStudents(courseId));
            detail.setAssignmentStats(detailDAO.getAssignmentStats(courseId));
        }
        return detail;
    }
}
