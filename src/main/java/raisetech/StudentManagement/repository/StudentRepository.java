package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 学生データベース操作
 */
@Mapper
public interface StudentRepository {

  /**
   * 有効な学生一覧を取得（論理削除されていない学生のみ）
   * SQLはStudentRepository.xmlに定義
   */
  List<Student> getActiveStudents();

  /**
   * 学生IDで学生情報を取得
   * SQLはStudentRepository.xmlに定義
   */
  Student getStudentById(int id);

  /**
   * 全コース情報を取得（Converter結合用）
   * SQLはStudentRepository.xmlに定義
   */
  List<StudentCourse> getAllCourses();

  /**
   * 特定学生のコース情報を取得（複数コース対応）
   * SQLはStudentRepository.xmlに定義
   */
  List<StudentCourse> getCoursesByStudentId(int studentId);

  /**
   * 学生情報を登録
   * SQLはStudentRepository.xmlに定義
   */
  int saveStudent(Student student);

  /**
   * コース情報を登録
   * SQLはStudentRepository.xmlに定義
   */
  int saveCourse(StudentCourse course);

  /**
   * 学生情報を更新
   * SQLはStudentRepository.xmlに定義
   */
  int updateStudent(Student student);

  /**
   * コース情報を更新（course.idをキーに使用）
   * SQLはStudentRepository.xmlに定義
   */
  int updateCourse(StudentCourse course);

  /**
   * 学生を論理削除
   * SQLはStudentRepository.xmlに定義
   */
  int deleteStudent(int id);

}