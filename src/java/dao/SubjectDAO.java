package dao;

import config.DBContext;
import model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject operations.
 */
public class SubjectDAO {

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM Subjects ORDER BY subject_code";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { list.add(map(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Subject getSubjectById(int id) {
        String sql = "SELECT * FROM Subjects WHERE subject_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean createSubject(Subject s) {
        String sql = "INSERT INTO Subjects (subject_code, subject_name, description) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE Subjects SET subject_code=?, subject_name=?, description=? WHERE subject_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDescription());
            ps.setInt(4, s.getSubjectId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Delete a subject. Throws exception if tied to courses.
     */
    public void deleteSubject(int id) throws Exception {
        String sql = "DELETE FROM Subjects WHERE subject_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Subject map(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setSubjectId(rs.getInt("subject_id"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setDescription(rs.getString("description"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
