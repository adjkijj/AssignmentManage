-- ============================================
-- Assignment Management System - Database Schema
-- SQL Server
-- ============================================

-- Create Database
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'AssignmentManageDB')
    CREATE DATABASE AssignmentManageDB;
GO

USE AssignmentManageDB;
GO

-- ============================================
-- Drop existing tables (in dependency order)
-- ============================================
IF OBJECT_ID('Group_Members', 'U') IS NOT NULL DROP TABLE Group_Members;
IF OBJECT_ID('Audit_Logs', 'U') IS NOT NULL DROP TABLE Audit_Logs;
IF OBJECT_ID('Notifications', 'U') IS NOT NULL DROP TABLE Notifications;
IF OBJECT_ID('Comments', 'U') IS NOT NULL DROP TABLE Comments;
IF OBJECT_ID('Submissions', 'U') IS NOT NULL DROP TABLE Submissions;
IF OBJECT_ID('Groups', 'U') IS NOT NULL DROP TABLE Groups;
IF OBJECT_ID('Assignments', 'U') IS NOT NULL DROP TABLE Assignments;
IF OBJECT_ID('Enrollments', 'U') IS NOT NULL DROP TABLE Enrollments;
IF OBJECT_ID('Courses', 'U') IS NOT NULL DROP TABLE Courses;
IF OBJECT_ID('Users', 'U') IS NOT NULL DROP TABLE Users;
GO

-- ============================================
-- 1. Users Table
-- ============================================
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    role NVARCHAR(20) NOT NULL CHECK (role IN ('student', 'instructor', 'admin')),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);
GO

-- ============================================
-- ============================================
-- 2. Semesters Table
-- ============================================
CREATE TABLE Semesters (
    semester_id INT IDENTITY(1,1) PRIMARY KEY,
    semester_name NVARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BIT DEFAULT 1
);
GO

-- ============================================
-- 3. Subjects Table
-- ============================================
CREATE TABLE Subjects (
    subject_id INT IDENTITY(1,1) PRIMARY KEY,
    subject_code NVARCHAR(20) NOT NULL UNIQUE,
    subject_name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================
-- 4. Courses Table (acts as "Class" — linked to Semester + Subject)
-- ============================================
CREATE TABLE Courses (
    course_id INT IDENTITY(1,1) PRIMARY KEY,
    course_code NVARCHAR(20) NOT NULL UNIQUE,
    course_name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    instructor_id INT NOT NULL,
    semester_id INT NULL,
    subject_id INT NULL,
    max_students INT DEFAULT 40,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Courses_Instructor FOREIGN KEY (instructor_id) REFERENCES Users(user_id),
    CONSTRAINT FK_Courses_Semester FOREIGN KEY (semester_id) REFERENCES Semesters(semester_id),
    CONSTRAINT FK_Courses_Subject FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id)
);
GO

-- ============================================
-- 3. Enrollments Table
-- ============================================
CREATE TABLE Enrollments (
    enrollment_id INT IDENTITY(1,1) PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrolled_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Enrollments_Student FOREIGN KEY (student_id) REFERENCES Users(user_id),
    CONSTRAINT FK_Enrollments_Course FOREIGN KEY (course_id) REFERENCES Courses(course_id),
    CONSTRAINT UQ_Enrollment UNIQUE (student_id, course_id)
);
GO

-- ============================================
-- 4. Assignments Table
-- ============================================
CREATE TABLE Assignments (
    assignment_id INT IDENTITY(1,1) PRIMARY KEY,
    course_id INT NOT NULL,
    instructor_id INT NOT NULL,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    deadline DATETIME NOT NULL,
    soft_deadline DATETIME NULL,
    hard_deadline DATETIME NULL,
    attachment_path NVARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Assignments_Course FOREIGN KEY (course_id) REFERENCES Courses(course_id),
    CONSTRAINT FK_Assignments_Instructor FOREIGN KEY (instructor_id) REFERENCES Users(user_id)
);
GO

-- ============================================
-- 5. Submissions Table (EXTENDED: version control, status, late flag, group support)
-- ============================================
CREATE TABLE Submissions (
    submission_id INT IDENTITY(1,1) PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    file_path NVARCHAR(500) NOT NULL,
    submitted_at DATETIME NOT NULL DEFAULT GETDATE(),
    grade FLOAT,
    feedback NVARCHAR(MAX),
    graded_at DATETIME,
    version INT NOT NULL DEFAULT 1,
    status NVARCHAR(20) NOT NULL DEFAULT 'submitted', -- submitted, late, graded
    is_late BIT NOT NULL DEFAULT 0,
    group_id INT NULL,
    CONSTRAINT FK_Submissions_Assignment FOREIGN KEY (assignment_id) REFERENCES Assignments(assignment_id),
    CONSTRAINT FK_Submissions_Student FOREIGN KEY (student_id) REFERENCES Users(user_id)
    -- NOTE: UQ_Submission (assignment_id, student_id) removed to allow multiple versions
);
GO

-- ============================================
-- 6. Comments Table (EXTENDED: parent_comment_id for threading)
-- ============================================
CREATE TABLE Comments (
    comment_id INT IDENTITY(1,1) PRIMARY KEY,
    assignment_id INT NOT NULL,
    user_id INT NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    parent_comment_id INT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Comments_Assignment FOREIGN KEY (assignment_id) REFERENCES Assignments(assignment_id),
    CONSTRAINT FK_Comments_User FOREIGN KEY (user_id) REFERENCES Users(user_id),
    CONSTRAINT FK_Comments_Parent FOREIGN KEY (parent_comment_id) REFERENCES Comments(comment_id)
);
GO

-- ============================================
-- 7. Notifications Table (NEW)
-- ============================================
CREATE TABLE Notifications (
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    title NVARCHAR(100) NOT NULL,
    message NVARCHAR(MAX),
    is_read BIT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Notifications_User FOREIGN KEY (user_id) REFERENCES Users(user_id)
);
GO

-- ============================================
-- 8. Audit Logs Table (NEW)
-- ============================================
CREATE TABLE Audit_Logs (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    action NVARCHAR(50) NOT NULL,
    target_id INT,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_AuditLogs_User FOREIGN KEY (user_id) REFERENCES Users(user_id)
);
GO

-- ============================================
-- 9. Groups Table (NEW - for group assignments)
-- ============================================
CREATE TABLE Groups (
    group_id INT IDENTITY(1,1) PRIMARY KEY,
    assignment_id INT NOT NULL,
    group_name NVARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Groups_Assignment FOREIGN KEY (assignment_id) REFERENCES Assignments(assignment_id)
);
GO

-- ============================================
-- 10. Group Members Table (NEW)
-- ============================================
CREATE TABLE Group_Members (
    group_id INT NOT NULL,
    student_id INT NOT NULL,
    PRIMARY KEY (group_id, student_id),
    CONSTRAINT FK_GroupMembers_Group FOREIGN KEY (group_id) REFERENCES Groups(group_id),
    CONSTRAINT FK_GroupMembers_Student FOREIGN KEY (student_id) REFERENCES Users(user_id)
);
GO

-- Add FK from Submissions.group_id to Groups
ALTER TABLE Submissions
ADD CONSTRAINT FK_Submissions_Group FOREIGN KEY (group_id) REFERENCES Groups(group_id);
GO

-- ============================================
-- 11. Password Reset Tokens Table (Feature 1)
-- ============================================
CREATE TABLE PasswordResetTokens (
    token_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    token NVARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    is_used BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_ResetToken_User FOREIGN KEY (user_id) REFERENCES Users(user_id)
);
GO

-- ============================================
-- 12. Course Materials Table (Feature 3)
-- ============================================
CREATE TABLE CourseMaterials (
    material_id INT IDENTITY(1,1) PRIMARY KEY,
    course_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(500),
    file_path NVARCHAR(500),
    external_url NVARCHAR(500),
    material_type NVARCHAR(50),
    week_number INT DEFAULT 0,
    topic NVARCHAR(255),
    uploaded_by INT,
    uploaded_at DATETIME DEFAULT GETDATE(),
    is_visible BIT DEFAULT 1,
    CONSTRAINT FK_Material_Course FOREIGN KEY (course_id) REFERENCES Courses(course_id),
    CONSTRAINT FK_Material_User FOREIGN KEY (uploaded_by) REFERENCES Users(user_id)
);
GO

-- ============================================
-- Seed Data
-- ============================================

-- Admin user (password: admin123)
INSERT INTO Users (username, password, full_name, email, role)
VALUES ('admin', 'admin123', 'System Administrator', 'admin@university.edu', 'admin');

-- Sample Instructor
INSERT INTO Users (username, password, full_name, email, role)
VALUES ('instructor1', 'instructor123', 'Dr. John Smith', 'john.smith@university.edu', 'instructor');

-- Sample Students
INSERT INTO Users (username, password, full_name, email, role)
VALUES ('student1', 'student123', 'Alice Johnson', 'alice.johnson@university.edu', 'student');

INSERT INTO Users (username, password, full_name, email, role)
VALUES ('student2', 'student123', 'Bob Williams', 'bob.williams@university.edu', 'student');

-- Sample Semesters
INSERT INTO Semesters (semester_name, start_date, end_date) VALUES ('Spring 2026', '2026-01-15', '2026-04-15');
INSERT INTO Semesters (semester_name, start_date, end_date) VALUES ('Summer 2026', '2026-05-15', '2026-08-15');

-- Sample Subjects
INSERT INTO Subjects (subject_code, subject_name, description) VALUES ('PRJ301', 'Web Dev with Java', 'Building web apps using Servlet, JSP, and JDBC');
INSERT INTO Subjects (subject_code, subject_name, description) VALUES ('PRJ302', 'Advanced Web Dev', 'Advanced topics including frameworks and design patterns');

-- Sample Classes (linked to semesters and subjects)
INSERT INTO Courses (course_code, course_name, description, instructor_id, semester_id, subject_id, max_students)
VALUES ('SE1234', 'PRJ301 - Web Dev Spring26', 'Web Development with Java class.', 2, 1, 1, 40);

INSERT INTO Courses (course_code, course_name, description, instructor_id, semester_id, subject_id, max_students)
VALUES ('SE5678', 'PRJ302 - Adv Web Spring26', 'Advanced Web Development class.', 2, 1, 2, 35);

-- Sample Enrollments
INSERT INTO Enrollments (student_id, course_id) VALUES (3, 1);
INSERT INTO Enrollments (student_id, course_id) VALUES (4, 1);
INSERT INTO Enrollments (student_id, course_id) VALUES (3, 2);

-- Sample Assignment (with soft/hard deadlines)
INSERT INTO Assignments (course_id, instructor_id, title, description, deadline, soft_deadline, hard_deadline)
VALUES (1, 2, 'Assignment 1: Servlet Basics',
        'Create a simple servlet that handles GET and POST requests. Include form validation and session management.',
        '2026-04-15 23:59:00', '2026-04-15 23:59:00', '2026-04-18 23:59:00');

INSERT INTO Assignments (course_id, instructor_id, title, description, deadline, soft_deadline, hard_deadline)
VALUES (1, 2, 'Assignment 2: JSP and JSTL',
        'Build a small web application using JSP, JSTL, and custom tags. Apply MVC pattern.',
        '2026-05-01 23:59:00', '2026-05-01 23:59:00', '2026-05-04 23:59:00');

PRINT 'Database schema created and seed data inserted successfully!';
GO
