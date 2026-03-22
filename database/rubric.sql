CREATE TABLE Rubric (
  id INT IDENTITY PRIMARY KEY,
  assignment_id INT NOT NULL FOREIGN KEY REFERENCES Assignments(assignment_id),
  total_points FLOAT NOT NULL
);

CREATE TABLE RubricCriteria (
  id INT IDENTITY PRIMARY KEY,
  rubric_id INT NOT NULL FOREIGN KEY REFERENCES Rubric(id),
  criteria_name NVARCHAR(255) NOT NULL,
  description NVARCHAR(500),
  max_points FLOAT NOT NULL,
  weight_percent FLOAT NOT NULL
);

CREATE TABLE RubricGrade (
  id INT IDENTITY PRIMARY KEY,
  submission_id INT NOT NULL FOREIGN KEY REFERENCES Submissions(submission_id),
  criteria_id INT NOT NULL FOREIGN KEY REFERENCES RubricCriteria(id),
  points_earned FLOAT NOT NULL,
  comment NVARCHAR(500)
);
