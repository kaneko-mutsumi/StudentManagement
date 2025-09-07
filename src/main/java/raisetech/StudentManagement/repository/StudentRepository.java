package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 学生データベース操作
 */
@Mapper
public interface StudentRepository {

  /**
   * 有効な学生一覧を取得（論理削除されていない学生のみ）
   * ORDER BY id でページング対応
   */
  @Select("""
      SELECT id, name, kanaName, nickname, email, area, age, sex, remark, deleted 
      FROM students 
      WHERE deleted IS NULL OR deleted = 0 
      ORDER BY id
      """)
  List<Student> getActiveStudents();

  /**
   * 学生IDで学生情報を取得
   */
  @Select("""
      SELECT id, name, kanaName, nickname, email, area, age, sex, remark, deleted 
      FROM students 
      WHERE id = #{id}
      """)
  Student getStudentById(int id);

  /**
   * 全コース情報を取得（Converter結合用）
   * ORDER BY student_id, id でグループ化に最適化
   */
  @Select("""
      SELECT 
        id,
        student_id as studentId,
        course_name as courseName,
        course_start_at as courseStartAt,
        course_end_at as courseEndAt
      FROM students_courses 
      ORDER BY student_id, id
      """)
  List<StudentCourse> getAllCourses();

  /**
   * 特定学生のコース情報を取得（複数コース対応）
   * 戻り値：List<StudentCourse>（指示書準拠）
   */
  @Select("""
      SELECT 
        id,
        student_id as studentId,
        course_name as courseName,
        course_start_at as courseStartAt,
        course_end_at as courseEndAt
      FROM students_courses 
      WHERE student_id = #{studentId}
      ORDER BY id
      """)
  List<StudentCourse> getCoursesByStudentId(int studentId);

  /**
   * 学生情報を登録
   */
  @Insert("""
      INSERT INTO students(name, kanaName, nickname, email, area, age, sex, remark) 
      VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int saveStudent(Student student);

  /**
   * コース情報を登録
   */
  @Insert("""
      INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at) 
      VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})
      """)
  int saveCourse(StudentCourse course);

  /**
   * 学生情報を更新
   */
  @Update("""
      UPDATE students 
      SET name = #{name}, kanaName = #{kanaName}, nickname = #{nickname}, 
          email = #{email}, area = #{area}, age = #{age}, sex = #{sex}, remark = #{remark} 
      WHERE id = #{id}
      """)
  int updateStudent(Student student);

  /**
   * コース情報を更新（course.idをキーに使用）
   * 修正意図：student_id単位の更新は複数コース環境で危険なため禁止
   */
  @Update("""
      UPDATE students_courses 
      SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} 
      WHERE id = #{id}
      """)
  int updateCourse(StudentCourse course);

  /**
   * 学生を論理削除
   */
  @Update("UPDATE students SET deleted = 1 WHERE id = #{id}")
  int deleteStudent(int id);
}