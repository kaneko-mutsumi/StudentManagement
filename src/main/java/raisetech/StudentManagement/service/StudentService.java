package raisetech.StudentManagement.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 学生サービス
 */
@Service
@Transactional
public class StudentService {

  private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

  @Autowired
  private StudentRepository repository;

  /**
   * 学生一覧取得
   */
  @Transactional(readOnly = true)
  public List<Student> getStudents() {
    logger.info("学生一覧取得開始");
    List<Student> students = repository.getActiveStudents();
    logger.info("学生一覧取得完了: {}件", students.size());
    return students;
  }

  /**
   * 全コース一覧取得
   */
  @Transactional(readOnly = true)
  public List<StudentsCourses> getCourses() {
    logger.info("コース一覧取得開始");
    List<StudentsCourses> courses = repository.getAllCourses();
    logger.info("コース一覧取得完了: {}件", courses.size());
    return courses;
  }

  /**
   * 編集用学生フォーム取得
   */
  @Transactional(readOnly = true)
  public StudentForm getStudentForm(int id) {
    logger.info("学生詳細取得開始: ID={}", id);

    Student student = repository.getStudentById(id);
    if (student == null) {
      logger.warn("学生が見つかりません: ID={}", id);
      throw new RuntimeException("学生が見つかりません: ID=" + id);
    }

    List<StudentsCourses> courses = repository.getCoursesByStudentId(id);
    StudentsCourses primaryCourse = courses.isEmpty() ? null : courses.get(0);

    logger.info("学生詳細取得完了: ID={}, コース数={}", id, courses.size());
    return convertToForm(student, primaryCourse);
  }

  /**
   * 学生新規登録
   */
  public void registerStudent(StudentForm form) {
    logger.info("学生登録開始: {}", form.getName());

    Student student = convertToStudent(form);
    int studentRows = repository.saveStudent(student);

    if (studentRows != 1) {
      logger.error("学生登録失敗: 更新件数={}", studentRows);
      throw new RuntimeException("学生登録に失敗しました");
    }

    StudentsCourses course = convertToCourse(form);
    course.setStudentId(student.getId());
    int courseRows = repository.saveCourse(course);

    if (courseRows != 1) {
      logger.error("コース登録失敗: 更新件数={}", courseRows);
      throw new RuntimeException("コース登録に失敗しました");
    }

    logger.info("学生登録完了: ID={}, 名前={}", student.getId(), student.getName());
  }

  /**
   * 学生情報更新
   */
  public void updateStudent(StudentForm form) {
    logger.info("学生更新開始: ID={}", form.getId());

    Student student = convertToStudent(form);
    int studentRows = repository.updateStudent(student);

    if (studentRows != 1) {
      logger.warn("学生更新対象なし: ID={}", form.getId());
    }

    // 主コースの更新（course.idをキーに使用）
    if (form.getCourseId() != null) {
      StudentsCourses course = convertToCourse(form);
      int courseRows = repository.updateCourse(course);

      if (courseRows != 1) {
        logger.warn("コース更新対象なし: コースID={}", form.getCourseId());
      }
    }

    logger.info("学生更新完了: ID={}", form.getId());
  }

  /**
   * 学生論理削除
   */
  public void deleteStudent(int id) {
    logger.info("学生削除開始: ID={}", id);

    int rows = repository.deleteStudent(id);
    if (rows != 1) {
      logger.warn("削除対象なし: ID={}", id);
      throw new RuntimeException("削除対象の学生が見つかりません");
    }

    logger.info("学生削除完了: ID={}", id);
  }

  // 変換メソッド群
  private StudentForm convertToForm(Student student, StudentsCourses course) {
    StudentForm form = new StudentForm();
    form.setId(student.getId());
    form.setName(student.getName());
    form.setKanaName(student.getKanaName());
    form.setNickname(student.getNickname());
    form.setEmail(student.getEmail());
    form.setArea(student.getArea());
    form.setAge(student.getAge());
    form.setSex(student.getSex());
    form.setRemark(student.getRemark());

    if (course != null) {
      form.setCourseId(course.getId());
      form.setCourseName(course.getCourseName());
      form.setCourseStartAt(course.getCourseStartAt());
      form.setCourseEndAt(course.getCourseEndAt());
    }

    return form;
  }

  private Student convertToStudent(StudentForm form) {
    Student student = new Student();
    // 新規登録時はIDがnullなので設定しない、更新時のみ設定
    if (form.getId() != null) {
      student.setId(form.getId());
    }
    student.setName(form.getName());
    student.setKanaName(form.getKanaName());
    student.setNickname(form.getNickname());
    student.setEmail(form.getEmail());
    student.setArea(form.getArea());
    student.setAge(form.getAge());
    student.setSex(form.getSex());
    student.setRemark(form.getRemark());
    return student;
  }

  private StudentsCourses convertToCourse(StudentForm form) {
    StudentsCourses course = new StudentsCourses();

    // 新規登録時はCourseIDがnullなので設定しない
    if (form.getCourseId() != null) {
      course.setId(form.getCourseId());
    }

    course.setCourseName(form.getCourseName());
    course.setCourseStartAt(form.getCourseStartAt());
    course.setCourseEndAt(form.getCourseEndAt());
    return course;
  }
}