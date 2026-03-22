package dao;

import config.DBContext;
import model.Rubric;
import model.RubricCriteria;
import model.RubricGrade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RubricDAO {

    public Rubric findByAssignment(int assignmentId) {
        String sql = "SELECT * FROM Rubric WHERE assignment_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rubric r = new Rubric();
                    r.setId(rs.getInt("id"));
                    r.setAssignmentId(rs.getInt("assignment_id"));
                    r.setTotalPoints(rs.getDouble("total_points"));
                    return r;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RubricCriteria> findCriteriaByRubric(int rubricId) {
        List<RubricCriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM RubricCriteria WHERE rubric_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rubricId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RubricCriteria rc = new RubricCriteria();
                    rc.setId(rs.getInt("id"));
                    rc.setRubricId(rs.getInt("rubric_id"));
                    rc.setCriteriaName(rs.getString("criteria_name"));
                    rc.setDescription(rs.getString("description"));
                    rc.setMaxPoints(rs.getDouble("max_points"));
                    rc.setWeightPercent(rs.getDouble("weight_percent"));
                    list.add(rc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int saveRubric(Rubric r) {
        String sql = "INSERT INTO Rubric (assignment_id, total_points) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getAssignmentId());
            ps.setDouble(2, r.getTotalPoints());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateRubric(Rubric r) {
        String sql = "UPDATE Rubric SET total_points = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, r.getTotalPoints());
            ps.setInt(2, r.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCriteriaByRubric(int rubricId) {
        String sql = "DELETE FROM RubricCriteria WHERE rubric_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rubricId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCriteria(RubricCriteria rc) {
        String sql = "INSERT INTO RubricCriteria (rubric_id, criteria_name, description, max_points, weight_percent) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rc.getRubricId());
            ps.setString(2, rc.getCriteriaName());
            ps.setString(3, rc.getDescription());
            ps.setDouble(4, rc.getMaxPoints());
            ps.setDouble(5, rc.getWeightPercent());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RubricGrade> findGradesBySubmission(int submissionId) {
        List<RubricGrade> list = new ArrayList<>();
        String sql = "SELECT * FROM RubricGrade WHERE submission_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RubricGrade rg = new RubricGrade();
                    rg.setId(rs.getInt("id"));
                    rg.setSubmissionId(rs.getInt("submission_id"));
                    rg.setCriteriaId(rs.getInt("criteria_id"));
                    rg.setPointsEarned(rs.getDouble("points_earned"));
                    rg.setComment(rs.getString("comment"));
                    list.add(rg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void clearGradesBySubmission(int submissionId) {
        String sql = "DELETE FROM RubricGrade WHERE submission_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRubricGrade(RubricGrade rg) {
        String sql = "INSERT INTO RubricGrade (submission_id, criteria_id, points_earned, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rg.getSubmissionId());
            ps.setInt(2, rg.getCriteriaId());
            ps.setDouble(3, rg.getPointsEarned());
            ps.setString(4, rg.getComment());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
