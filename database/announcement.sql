CREATE TABLE Announcements (
    announcement_id INT PRIMARY KEY IDENTITY(1,1),
    course_id INT NULL,
    instructor_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    target_role VARCHAR(20) DEFAULT 'all',
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES Users(user_id)
);
