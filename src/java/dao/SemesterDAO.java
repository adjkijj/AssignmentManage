package dao;

import config.DBContext;
import model.Semester;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Semester operations.
 */
public class SemesterDAO {

    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT * FROM Semesters ORDER BY start_date DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { list.add(map(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Semester> getActiveSemesters() {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT * FROM Semesters WHERE is_active = 1 ORDER BY start_date DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { list.add(map(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Semester getSemesterById(int id) {
        String sql = "SELECT * FROM Semesters WHERE semester_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean createSemester(Semester s) {
        String sql = "INSERT INTO Semesters (semester_name, start_date, end_date) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSemesterName());
            ps.setDate(2, new java.sql.Date(s.getStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(s.getEndDate().getTime()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateSemester(Semester s) {
        String sql = "UPDATE Semesters SET semester_name=?, start_date=?, end_date=? WHERE semester_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSemesterName());
            ps.setDate(2, new java.sql.Date(s.getStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(s.getEndDate().getTime()));
            ps.setInt(4, s.getSemesterId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean toggleActive(int id) {
        String sql = "UPDATE Semesters SET is_active = CASE WHEN is_active=1 THEN 0 ELSE 1 END WHERE semester_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Delete a semester. Throws exception if tied to courses.
     */
    public void deleteSemester(int id) throws Exception {
        String sql = "DELETE FROM Semesters WHERE semester_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Semester map(ResultSet rs) throws SQLException {
        Semester s = new Semester();
        s.setSemesterId(rs.getInt("semester_id"));
        s.setSemesterName(rs.getString("semester_name"));
        s.setStartDate(rs.getDate("start_date"));
        s.setEndDate(rs.getDate("end_date"));
        s.setIsActive(rs.getBoolean("is_active"));
        return s;
    }
}
