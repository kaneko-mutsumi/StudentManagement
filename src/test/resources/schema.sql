-- 学生テーブル
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    kanaName VARCHAR(50),
    nickname VARCHAR(25),
    email VARCHAR(255) NOT NULL,
    area VARCHAR(50),
    age INT NOT NULL,
    sex VARCHAR(10) DEFAULT NULL,
    remark VARCHAR(255),
    deleted TINYINT DEFAULT NULL
);

-- コーステーブル
CREATE TABLE students_courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_name VARCHAR(50) NOT NULL,
    course_start_at DATE,
    course_end_at DATE
);
