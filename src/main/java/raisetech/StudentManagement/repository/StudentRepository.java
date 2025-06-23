package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

@Mapper
public interface StudentRepository {

  @Select("""
      SELECT 
        id,
        name,
        kanaName,
        nickname,
        email,
        area,
        age,
        sex,
        remark,
        deleted
      FROM students 
      WHERE deleted IS NULL OR deleted = 0
      """)
  List<Student> search();

  @Select("""
      SELECT 
        id,
        name,
        kanaName,
        nickname,
        email,
        area,
        age,
        sex,
        remark,
        deleted
      FROM students 
      WHERE id = #{id}
      """)
  Student findById(int id);

  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentsCourses();

  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  StudentsCourses findCourseByStudentId(int studentId);

  @Insert("""
        INSERT INTO students(
          name, kanaName, nickname, email, area,
          age, sex, remark, deleted
        ) VALUES (
          #{name}, #{kanaName}, #{nickname}, #{email}, #{area},
          #{age}, #{sex}, #{remark}, #{deleted}
        )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertStudent(Student student);

  @Insert("""
          INSERT INTO students_courses(
          student_id, course_name, course_start_at, course_end_at
          )VALUES(
          #{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt}
          )
      """)
  void insertCourse(StudentsCourses course);

  /**
   * 学生情報を更新する
   */
  @Update("""
      UPDATE students 
      SET name = #{name}, 
          kanaName = #{kanaName},
          nickname = #{nickname},
          email = #{email},
          area = #{area},
          age = #{age},
          sex = #{sex},
          remark = #{remark}
      WHERE id = #{id}
      """)
  void updateStudent(Student student);

  /**
   * コース情報を更新する
   */
  @Update("""
      UPDATE students_courses 
      SET course_name = #{courseName},
          course_start_at = #{courseStartAt},
          course_end_at = #{courseEndAt}
      WHERE student_id = #{studentId}
      """)
  void updateCourse(StudentsCourses course);
}