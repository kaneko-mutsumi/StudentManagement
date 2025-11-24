-- テスト用学生データ
INSERT INTO students (name, kanaName, nickname, email, area, age, sex, remark, deleted) VALUES
('テスト太郎', 'テストタロウ', 'たろう', 'taro@test.com', '東京', 20, '男性', NULL, NULL),
('テスト花子', 'テストハナコ', 'はなちゃん', 'hanako@test.com', '大阪', 22, '女性', NULL, NULL),
('削除済み太郎', 'サクジョズミタロウ', NULL, 'deleted@test.com', '福岡', 25, '男性', NULL, 1);

-- テスト用コースデータ
INSERT INTO students_courses (student_id, course_name, course_start_at, course_end_at) VALUES
(1, 'Java入門', '2025-01-01', '2025-03-31'),
(2, 'Spring実践', '2025-04-01', '2025-06-30');
