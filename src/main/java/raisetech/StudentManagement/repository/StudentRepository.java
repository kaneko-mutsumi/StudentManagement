package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

/**
 * 学生データベース操作
 */
@Mapper
public interface StudentRepository {

  @Select("""
      SELECT id, name, kanaName, nickname, email, area, age, sex, remark, deleted 
      FROM students 
      WHERE deleted IS NULL OR deleted = 0 
      ORDER BY id
      """)
  List<Student> getActiveStudents();

  @Select("""
      SELECT id, name, kanaName, nickname, email, area, age, sex, remark, deleted 
      FROM students 
      WHERE id = #{id}
      """)
  Student getStudent(int id);

  @Select("""
      SELECT 
        id,
        student_id as studentId,
        course_name as courseName,
        course_start_at as courseStartAt,
        course_end_at as courseEndAt
      FROM students_courses 
      ORDER BY id
      """)
  List<StudentsCourses> getAllCourses();

  @Select("""
      SELECT 
        id,
        student_id as studentId,
        course_name as courseName,
        course_start_at as courseStartAt,
        course_end_at as courseEndAt
      FROM students_courses 
      WHERE student_id = #{studentId}
      """)
  StudentsCourses getCourse(int studentId);

  @Insert("""
      INSERT INTO students(name, kanaName, nickname, email, area, age, sex, remark) 
      VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void saveStudent(Student student);

  @Insert("""
      INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at) 
      VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})
      """)
  void saveCourse(StudentsCourses course);

  @Update("""
      UPDATE students 
      SET name = #{name}, kanaName = #{kanaName}, nickname = #{nickname}, 
          email = #{email}, area = #{area}, age = #{age}, sex = #{sex}, remark = #{remark} 
      WHERE id = #{id}
      """)
  void updateStudent(Student student);

  @Update("""
      UPDATE students_courses 
      SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} 
      WHERE student_id = #{studentId}
      """)
  void updateCourse(StudentsCourses course);

  @Update("UPDATE students SET deleted = 1 WHERE id = #{id}")
  void cancelStudent(int id);
}