CREATE TABLE IF NOT EXISTS students
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  furigana VARCHAR(100) NOT NULL,
  nickname VARCHAR(100),
  email VARCHAR(100) NOT NULL,
  city VARCHAR(100),
  age INT,
  gender VARCHAR (10),
  remark TEXT,
  isDeleted boolean
);

CREATE TABLE IF NOT EXISTS students_courses
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  course_name VARCHAR(100) NOT NULL,
  start_date TIMESTAMP,
  end_date TIMESTAMP
);