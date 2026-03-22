package dao;

import config.DBContext;
import model.FeedbackTemplate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for FeedbackTemplates — Feature 6.
 * Returns shared templates (instructor_id IS NULL) + instructor-specific templates.
 */
public class FeedbackTemplateDAO {

    public List<FeedbackTemplate> getTemplates(Integer instructorId) {
        List<FeedbackTemplate> list = new ArrayList<>();
        String sql = "SELECT * FROM FeedbackTemplates "
                   + "WHERE instructor_id IS NULL "
                   + (instructorId != null ? "OR instructor_id = ? " : "")
                   + "ORDER BY category, template_id";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (instructorId != null) {
                ps.setInt(1, instructorId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeedbackTemplate t = new FeedbackTemplate();
                    t.setTemplateId(rs.getInt("template_id"));
                    int instId = rs.getInt("instructor_id");
                    if (!rs.wasNull()) t.setInstructorId(instId);
                    t.setCategory(rs.getString("category"));
                    t.setContent(rs.getString("content"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
