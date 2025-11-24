package raisetech.StudentManagement.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 学生管理サービスクラス（REST API対応版）
 */
@Service
@Transactional
public class StudentService {

  private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

  @Autowired
  private StudentRepository repository;

  @Autowired
  private StudentConverter converter;

  // ============================================
  // データ取得系のメソッド（読み取り専用）
  // ============================================

  @Transactional(readOnly = true)
  public List<Student> getStudents() {
    logger.info("学生一覧取得開始");

    try {
      List<Student> students = repository.getActiveStudents();
      logger.info("学生一覧取得完了: {}件", students.size());
      return students;

    } catch (Exception e) {
      logger.error("学生一覧取得でシステムエラーが発生", e);
      throw new RuntimeException("学生一覧の取得に失敗しました", e);
    }
  }

  @Transactional(readOnly = true)
  public List<StudentCourse> getCourses() {
    logger.info("コース一覧取得開始");

    try {
      List<StudentCourse> courses = repository.getAllCourses();
      logger.info("コース一覧取得完了： 取得件数={}", courses.size());
      return courses;

    } catch (Exception e) {
      logger.error("コース一覧取得でシステムエラーが発生", e);
      throw new RuntimeException("コース一覧の取得に失敗しました", e);
    }
  }

  @Transactional(readOnly = true)
  public StudentForm getStudentForm(int id) {
    logger.info("学生詳細取得開始: ID={}", id);

    try {
      Student student = repository.getStudentById(id);

      if (student == null) {
        logger.warn("学生が見つかりません: ID={}", id);
        throw new ResourceNotFoundException("学生が見つかりません: ID=" + id);
      }

      List<StudentCourse> courses = repository.getCoursesByStudentId(id);
      StudentCourse primaryCourse = courses.isEmpty() ? null : courses.get(0);

      // ✅ 修正: converterを使用
      StudentForm form = converter.toForm(student, primaryCourse);

      logger.info("学生詳細取得完了: ID={}, コース数={}", id, courses.size());
      return form;

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      logger.error("学生詳細取得でシステムエラーが発生: ID={}", id, e);
      throw new RuntimeException("学生情報の取得に失敗しました", e);
    }
  }

  // ============================================
  // データ更新系のメソッド（書き込み処理）
  // ============================================

  public void registerStudent(StudentForm form) {
    if (form == null) {
      throw new IllegalArgumentException("学生情報が入力されていません");
    }

    logger.info("学生登録開始: 名前={}", form.getName());

    try {
      // ✅ 修正: converterを使用
      Student student = converter.toStudent(form);
      int studentRows = repository.saveStudent(student);

      if (studentRows != 1) {
        logger.error("学生登録で予期しない更新件数: 期待=1, 実際={}", studentRows);
        throw new RuntimeException("学生登録に失敗しました");
      }

      StudentCourse course = converter.toCourse(form);
      course.setStudentId(student.getId());
      int courseRows = repository.saveCourse(course);

      if (courseRows != 1) {
        logger.error("コース登録で予期しない更新件数: 期待=1, 実際={}", courseRows);
        throw new RuntimeException("学生登録に失敗しました");
      }

      logger.info("学生登録完了: ID={}, 名前={}", student.getId(), student.getName());

      logger.info("学生登録完了: ID={}, 名前={}", student.getId(), student.getName());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      logger.error("学生登録でシステムエラーが発生： 名前={}", form.getName(), e);
      throw new RuntimeException("学生の登録に失敗しました", e);
    }
  }

  public void updateStudent(StudentForm form) {
    if (form == null || form.getId() == null) {
      throw new IllegalArgumentException("更新対象の学生IDが指定されていません");
    }

    logger.info("学生更新開始: 対象ID={}", form.getId());

    try {
      Student student = converter.toStudent(form);
      int studentRows = repository.updateStudent(student);

      if (studentRows != 1) {
        logger.warn("学生更新対象が見つかりません: ID={}", form.getId());
        throw new ResourceNotFoundException("学生が見つかりません: ID=" + form.getId());
      }

      if (form.getCourseId() != null) {
        logger.info("受信したフォーム情報: courseId={}, courseName={}", form.getCourseId(),
            form.getCourseName());
        logger.info("コース更新実行: コースID={}", form.getCourseId());

        StudentCourse course = converter.toCourse(form);
        logger.info("更新するコース情報: ID={}, コース名={}", course.getId(),
            course.getCourseName());
        int courseRows = repository.updateCourse(course);
        logger.info("コース更新結果: {}件", courseRows);

        if (courseRows != 1) {
          logger.warn("コース更新対象が見つかりません: コースID={}", form.getCourseId());
        }
      }

      logger.info("学生更新完了: 対象ID={}", form.getId());

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      logger.error("学生更新でシステムエラーが発生: ID={}", form.getId(), e);
      throw new RuntimeException("学生情報の更新に失敗しました", e);
    }
  }

  public void deleteStudent(int id) {
    logger.info("学生削除開始: 対象ID={}", id);

    try {
      int rows = repository.deleteStudent(id);

      if (rows != 1) {
        logger.warn("削除対象の学生が見つかりません: ID={}", id);
        throw new ResourceNotFoundException("学生が見つかりません: ID=" + id);
      }

      logger.info("学生削除完了: 対象ID={}", id);

    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      logger.error("学生削除でシステムエラーが発生: ID={}", id, e);
      throw new RuntimeException("学生の削除に失敗しました", e);
    }
  }
}