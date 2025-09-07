package raisetech.StudentManagement.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 学生管理サービスクラス（REST API対応版）
 */
@Service
@Transactional
public class StudentService {

  /**
   * ログ出力用のオブジェクト コントローラーのログと合わせて統一された形式で出力
   */
  private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

  /**
   * データベース操作用のリポジトリ
   */
  @Autowired
  private StudentRepository repository;

  // ============================================
  // データ取得系のメソッド（読み取り専用）
  // ============================================

  /**
   * 有効な学生一覧取得
   *
   * @return List<Student> 学生リスト
   * @throws RuntimeException システムエラーの場合
   */

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

  /**
   * 全コース一覧取得
   *
   * @return List<StudentCourse> コースリスト
   * @throws RuntimeException システムエラーの場合
   */
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

  /**
   * 特性の学生の詳細情報を取得
   *
   * @param id 学生ID
   * @return StudentForm 編集用フォームデータ
   * @throws RuntimeException 学生が見つからない、またはシステムエラーの場合
   */
  @Transactional(readOnly = true)
  public StudentForm getStudentForm(int id) {
    logger.info("学生詳細取得開始: ID={}", id);

    try {
      // ステップ1: 学生情報を取得
      Student student = repository.getStudentById(id);

      // ステップ2: 学生が存在しない場合はエラー
      if (student == null) {
        logger.warn("学生が見つかりません: ID={}", id);
        // コントローラーで404に変換される
        throw new RuntimeException("学生が見つかりません: ID=" + id);
      }

      // ステップ3: その学生のコース情報を取得
      List<StudentCourse> courses = repository.getCoursesByStudentId(id);
      StudentCourse primaryCourse = courses.isEmpty() ? null : courses.get(0);

      // ステップ4: フォーム形式に変換
      StudentForm form = convertToForm(student, primaryCourse);

      logger.info("学生詳細取得完了: ID={}, コース数={}", id, courses.size());
      return form;
    } catch (RuntimeException e) {
      // ビジネス例外（学生未発見など）はそのまま再スロー
      throw e;
    } catch (Exception e) {
      logger.error("学生詳細取得でシステムエラーが発生:ID={}, id, e");
      throw new RuntimeException("学生情報の取得に失敗しました", e);
    }
  }

  // ============================================
  // データ更新系のメソッド（書き込み処理）
  // ============================================


  /**
   * 学生新規登録
   *
   * @param form 登録する学生情報
   * @throws RuntimeException 登録処理でエラーが発生した場合
   */
  public void registerStudent(StudentForm form) {
    // 入力チェック
    if (form == null) {
      throw new RuntimeException("学生情報が入力されていません");
    }

    logger.info("学生登録開始: 名前={}", form.getName());

    try {

      // ステップ1: 学生情報の登録
      Student student = convertToStudent(form);
      int studentRows = repository.saveStudent(student);

      // 更新件数の検証（1件登録されることを期待）
      if (studentRows != 1) {
        logger.error("学生登録で予期しない更新件数: 期待=1, 実際={}", studentRows);
        throw new RuntimeException("学生登録に失敗しました");
      }

      // ステップ2: コース情報の登録
      StudentCourse course = convertToCourse(form);
      course.setStudentId(student.getId());
      int courseRows = repository.saveCourse(course);

      // 更新件数の検証（1件登録されることを期待）
      if (studentRows != 1) {
        logger.error("学生登録で予期しない更新件数: 期待=1, 実際={}", studentRows);
        throw new RuntimeException("学生登録に失敗しました");
      }

      logger.info("学生登録完了: ID={}, 名前={}", student.getId(), student.getName());
    } catch (RuntimeException e) {
      // ビジネス例外はそのまま再スロー
      throw e;
    } catch (Exception e) {
      logger.error("学生登録でシステムエラーが発生： 名前={}", form.getName(), e);
      throw new RuntimeException("学生の登録に失敗しました", e);

    }
  }

  /**
   * 既存学生の情報を更新
   *
   * @param form 更新する学生情報（IDは必須）
   * @throws RuntimeException 更新処理でエラーが発生した場合
   */
  public void updateStudent(StudentForm form) {
    // 入力チェック
    if (form == null || form.getId() == null) {
      throw new RuntimeException("更新対象の学生IDが指定されていません");
    }

    logger.info("学生更新開始: 対象ID={}", form.getId());

    try {
      // ステップ1: 学生基本情報の更新
      Student student = convertToStudent(form);
      int studentRows = repository.updateStudent(student);

      // 更新結果の確認（REST APIでは更新対象なしもエラー扱い）
      if (studentRows != 1) {
        logger.warn("学生更新対象が見つかりません: ID={}", form.getId());
        throw new RuntimeException("更新対象の学生が見つかりません");
      }

// ステップ2: コース情報の更新（コースIDが指定されている場合のみ）
      if (form.getCourseId() != null) {
        logger.info("受信したフォーム情報: courseId={}, courseName={}", form.getCourseId(), form.getCourseName());
        logger.info("コース更新実行: コースID={}", form.getCourseId());
        StudentCourse course = convertToCourse(form);
        logger.info("更新するコース情報: ID={}, コース名={}", course.getId(), course.getCourseName());
        int courseRows = repository.updateCourse(course);
        logger.info("コース更新結果: {}件", courseRows);

        if (courseRows != 1) {
          logger.warn("コース更新対象が見つかりません: コースID={}", form.getCourseId());
          // コース情報の更新は警告のみ（学生情報は更新済み）
        }
      }

      logger.info("学生更新完了: 対象ID={}", form.getId());

    } catch (RuntimeException e) {
      // ビジネス例外はそのまま再スロー
      throw e;
    } catch (Exception e) {
      logger.error("学生更新でシステムエラーが発生: ID={}", form.getId(), e);
      throw new RuntimeException("学生情報の更新に失敗しました", e);
    }
  }

  /**
   * 学生を論理削除
   *
   * @param id 削除対象の学生ID
   * @throws RuntimeException 削除対象が見つからない、またはシステムエラーの場合
   */
  public void deleteStudent(int id) {
    logger.info("学生削除開始: 対象ID={}", id);

    try {
      // データベースで論理削除を実行
      int rows = repository.deleteStudent(id);

      // 削除結果の確認
      if (rows != 1) {
        logger.warn("削除対象の学生が見つかりません: ID={}", id);
        throw new RuntimeException("削除対象の学生が見つかりません");
      }

      logger.info("学生削除完了: 対象ID={}", id);

    } catch (RuntimeException e) {
      // ビジネス例外はそのまま再スロー
      throw e;
    } catch (Exception e) {
      logger.error("学生削除でシステムエラーが発生: ID={}", id, e);
      throw new RuntimeException("学生の削除に失敗しました", e);
    }
  }

  // ============================================
  // プライベートメソッド群（変換処理）
  // ============================================

  /**
   * 学生情報とコース情報をフォーム形式に変換
   *
   * @param student 学生エンティティ
   * @param course コースエンティティ（null可能）
   * @return StudentForm REST API応答用フォーム
   */
  private StudentForm convertToForm(Student student, StudentCourse course) {
    StudentForm form = new StudentForm();

    // 学生基本情報の設定
    form.setId(student.getId());
    form.setName(student.getName());
    form.setKanaName(student.getKanaName());
    form.setNickname(student.getNickname());
    form.setEmail(student.getEmail());
    form.setArea(student.getArea());
    form.setAge(student.getAge());
    form.setSex(student.getSex());
    form.setRemark(student.getRemark());

    // コース情報の設定（NULL安全処理）
    if (course != null) {
      form.setCourseId(course.getId());
      form.setCourseName(course.getCourseName());
      form.setCourseStartAt(course.getCourseStartAt());
      form.setCourseEndAt(course.getCourseEndAt());
    }

    return form;
  }

  /**
   * フォーム情報を学生エンティティに変換
   *
   * 変換目的：
   * - REST APIリクエスト形式（StudentForm）
   * - ↓
   * - データベース保存形式（Student）
   *
   * @param form REST APIからの入力データ
   * @return Student データベース保存用エンティティ
   */
  private Student convertToStudent(StudentForm form) {
    Student student = new Student();

    // 新規登録時はIDがnull、更新時のみ設定
    if (form.getId() != null) {
      student.setId(form.getId());
    }

    // フォームからエンティティへデータをコピー
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

  /**
   * フォーム情報をコースエンティティに変換
   *
   * 変換目的：
   * - REST APIリクエスト形式（StudentForm）
   * - ↓
   * - データベース保存形式（StudentCourse）
   *
   * @param form REST APIからの入力データ
   * @return StudentCourse データベース保存用エンティティ
   */
  private StudentCourse convertToCourse(StudentForm form) {
    StudentCourse course = new StudentCourse();

    // 新規登録時はCourseIDがnull、更新時のみ設定
    if (form.getCourseId() != null) {
      course.setId(form.getCourseId());
    }

    // フォームからエンティティへデータをコピー
    course.setCourseName(form.getCourseName());
    course.setCourseStartAt(form.getCourseStartAt());
    course.setCourseEndAt(form.getCourseEndAt());

    return course;
  }
}