package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import raisetech.StudentManagement.data.EnrollmentStatus;
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

  /**
   * 申込状況を登録
   */
  int saveEnrollmentStatus(EnrollmentStatus status);

  /**
   * 申込状況を更新
   */
  int updateEnrollmentStatus(EnrollmentStatus status);

  /**
   * コースIDで申込状況を取得
   */
  EnrollmentStatus getEnrollmentStatusByCourseId(int courseId);

  /**
   * 検索条件に基づいて学生を検索
   * @param name 名前（部分一致）
   * @param area 地域（部分一致）
   * @param courseName コース名（完全一致）
   * @param enrollmentStatus 申込状況（完全一致）
   * @return 検索結果の学生リスト
   */
  List<Student> searchStudents(
      @Param("name") String name,
      @Param("area") String area,
      @Param("courseName") String courseName,
      @Param("enrollmentStatus") String enrollmentStatus
  );
}