CREATE TABLE AuditLog (
    id INT IDENTITY PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    username NVARCHAR(100),
    action NVARCHAR(100) NOT NULL,       -- e.g. 'LOGIN','CREATE_COURSE','DELETE_USER'
    entity_type NVARCHAR(100),           -- e.g. 'Course','User','Assignment'
    entity_id INT,
    description NVARCHAR(500),
    ip_address NVARCHAR(50),
    created_at DATETIME DEFAULT GETDATE()
);
