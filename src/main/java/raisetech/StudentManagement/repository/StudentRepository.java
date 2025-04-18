package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;


@Mapper
public interface StudentRepository {


 // 年齢の範囲で生徒を検索
  @Select("SELECT * FROM students WHERE age BETWEEN #{from} AND #{to}")
  List<Student> findStudentsByAgeRange(@Param("from") int from, @Param("to") int to);

  // コース名で受講生を検索
  @Select("SELECT * FROM students_courses WHERE course_name = #{courseName}")
  List<StudentsCourses> findCoursesByName(@Param("courseName") String courseName);

}