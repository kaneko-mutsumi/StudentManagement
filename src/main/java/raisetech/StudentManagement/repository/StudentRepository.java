package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;



@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentsCourses();

  // 受講生のINSERT処理
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

  // コース情報のINSERT処理
  @Insert("""
          INSERT INTO students_courses(
          student_id, course_name, start_date, end_date
          )VALUES(
          #{studentId}, #{courseName}, #{startDate}, #{endDate}
          )
      """)
  void insertCourse(StudentsCourses course);

}