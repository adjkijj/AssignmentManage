package dao;

import config.DBContext;
import model.CourseMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for CourseMaterial CRUD operations.
 */
public class CourseMaterialDAO {

    /**
     * Get visible materials for a course (student view), ordered by week.
     */
    public List<CourseMaterial> getByCourse(int courseId) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name AS uploader_name "
                   + "FROM CourseMaterials m "
                   + "JOIN Courses c ON m.course_id = c.course_id "
                   + "LEFT JOIN Users u ON m.uploaded_by = u.user_id "
                   + "WHERE m.course_id = ? AND m.is_visible = 1 "
                   + "ORDER BY m.week_number, m.uploaded_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Get ALL materials for a course (instructor management view).
     */
    public List<CourseMaterial> getByCourseAll(int courseId) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT m.*, c.course_name, u.full_name AS uploader_name "
                   + "FROM CourseMaterials m "
                   + "JOIN Courses c ON m.course_id = c.course_id "
                   + "LEFT JOIN Users u ON m.uploaded_by = u.user_id "
                   + "WHERE m.course_id = ? "
                   + "ORDER BY m.week_number, m.uploaded_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean create(CourseMaterial m) {
        String sql = "INSERT INTO CourseMaterials (course_id, title, description, file_path, "
                   + "external_url, material_type, week_number, topic, uploaded_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getCourseId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getDescription());
            ps.setString(4, m.getFilePath());
            ps.setString(5, m.getExternalUrl());
            ps.setString(6, m.getMaterialType());
            ps.setInt(7, m.getWeekNumber());
            ps.setString(8, m.getTopic());
            ps.setInt(9, m.getUploadedBy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int materialId) {
        String sql = "DELETE FROM CourseMaterials WHERE material_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean toggleVisibility(int materialId) {
        String sql = "UPDATE CourseMaterials SET is_visible = CASE WHEN is_visible = 1 THEN 0 ELSE 1 END "
                   + "WHERE material_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private CourseMaterial mapRow(ResultSet rs) throws SQLException {
        CourseMaterial m = new CourseMaterial();
        m.setMaterialId(rs.getInt("material_id"));
        m.setCourseId(rs.getInt("course_id"));
        m.setTitle(rs.getString("title"));
        m.setDescription(rs.getString("description"));
        m.setFilePath(rs.getString("file_path"));
        m.setExternalUrl(rs.getString("external_url"));
        m.setMaterialType(rs.getString("material_type"));
        m.setWeekNumber(rs.getInt("week_number"));
        m.setTopic(rs.getString("topic"));
        m.setUploadedBy(rs.getInt("uploaded_by"));
        m.setUploadedAt(rs.getTimestamp("uploaded_at"));
        m.setIsVisible(rs.getBoolean("is_visible"));
        m.setCourseName(rs.getString("course_name"));
        m.setUploaderName(rs.getString("uploader_name"));
        return m;
    }
}
