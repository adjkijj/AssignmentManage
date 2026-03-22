-- =====================================================
-- FeedbackTemplates table + seed data (Feature 6)
-- Run this on your SQL Server database
-- =====================================================

CREATE TABLE FeedbackTemplates (
    template_id INT IDENTITY(1,1) PRIMARY KEY,
    instructor_id INT NULL,
    category NVARCHAR(20) NOT NULL,
    content NVARCHAR(500) NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (instructor_id) REFERENCES Users(user_id)
);

-- Seed shared templates (instructor_id = NULL)
INSERT INTO FeedbackTemplates (instructor_id, category, content) VALUES
(NULL, 'positive', N'Bài làm tốt, trình bày rõ ràng và logic.'),
(NULL, 'positive', N'Code sạch, có comment đầy đủ, đúng yêu cầu.'),
(NULL, 'positive', N'Ý tưởng sáng tạo, vượt yêu cầu đề bài.'),
(NULL, 'negative', N'Cần bổ sung comment trong code.'),
(NULL, 'negative', N'Trình bày chưa rõ ràng, cần sắp xếp lại cấu trúc.'),
(NULL, 'negative', N'Thiếu xử lý exception / validation đầu vào.'),
(NULL, 'negative', N'Chức năng chưa hoàn chỉnh theo yêu cầu đề bài.'),
(NULL, 'negative', N'Nộp muộn — trừ điểm theo quy định.'),
(NULL, 'neutral', N'Đạt yêu cầu cơ bản.'),
(NULL, 'neutral', N'Cần xem lại phần [...].') ;
