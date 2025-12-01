package raisetech.StudentManagement.repository;

import static org.junit.jupiter.api.Assertions.*;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.EnrollmentStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * StudentRepositoryのテストクラス
 * @Transactionalにより各テスト後に自動ロールバックされ、テストの独立性を担保
 */
@MybatisTest
@Transactional
class StudentRepositoryTest {

  @Autowired
  private StudentRepository repository;

  // ============ テストデータの定数 ============

  private static final int EXISTING_STUDENT_ID_1 = 1;
  private static final int EXISTING_STUDENT_ID_3 = 3;
  private static final int NON_EXISTING_STUDENT_ID = 999;

  private static final String STUDENT_NAME_TARO = "テスト太郎";
  private static final String STUDENT_NAME_HANAKO = "テスト花子";
  private static final String STUDENT_NAME_DELETED = "削除済み太郎";
  private static final String STUDENT_EMAIL_TARO = "taro@test.com";

  private static final String COURSE_NAME_JAVA = "Java入門";
  private static final String COURSE_NAME_SPRING = "Spring実践";
  private static final String COURSE_NAME_WEBAPP = "Webアプリ開発";
  private static final String COURSE_NAME_JAVA_ADVANCED = "Java応用";
  private OpenAPIDefinition logger;

  // ============ ヘルパーメソッド ============

  /**
   * リストに指定の名前の学生が含まれているか確認
   */
  private void assertStudentListContains(List<Student> students, String name) {
    assertTrue(students.stream().anyMatch(s -> name.equals(s.getName())),
        name + "が含まれているはず");
  }

  /**
   * リストに指定の名前の学生が含まれていないか確認
   */
  private void assertStudentListNotContains(List<Student> students, String name) {
    assertFalse(students.stream().anyMatch(s -> name.equals(s.getName())),
        name + "は含まれないはず");
  }

  /**
   * リストに指定のコース名が含まれているか確認
   */
  private void assertCourseListContains(List<StudentCourse> courses, String courseName) {
    assertTrue(courses.stream().anyMatch(c -> courseName.equals(c.getCourseName())),
        courseName + "が含まれているはず");
  }

  /**
   * deletedフィールドが削除状態（1またはtrue）であることを確認
   */
  private void assertDeletedIsTrue(Boolean deleted) {
    assertNotNull(deleted, "deletedフィールドが設定されているはず");
    String deletedStr = String.valueOf(deleted);
    assertTrue("1".equals(deletedStr) || "true".equals(deletedStr),
        "deletedが削除状態になっているはず（実際の値: " + deletedStr + ")");
  }

  /**
   * 新規学生データを作成（テストデータ生成用）
   */
  private Student createNewStudent(String name, String kanaName, String email, String area, int age) {
    Student student = new Student();
    student.setName(name);
    student.setKanaName(kanaName);
    student.setEmail(email);
    student.setArea(area);
    student.setAge(age);
    student.setSex("男性");
    return student;
  }

  /**
   * 新規コースデータを作成（テストデータ生成用）
   */
  private StudentCourse createNewCourse(int studentId, String courseName,
      LocalDate startDate, LocalDate endDate) {
    StudentCourse course = new StudentCourse();
    course.setStudentId(studentId);
    course.setCourseName(courseName);
    course.setCourseStartAt(startDate);
    course.setCourseEndAt(endDate);
    return course;
  }

  // ============ 検索系テスト ============

  @Test
  @DisplayName("有効な学生一覧が取得できること")
  void 有効な学生一覧が取得できること() {
    List<Student> students = repository.getActiveStudents();

    assertEquals(2, students.size(), "削除済みを除く2件が取得されるはず");
    assertStudentListContains(students, STUDENT_NAME_TARO);
    assertStudentListContains(students, STUDENT_NAME_HANAKO);
    assertStudentListNotContains(students, STUDENT_NAME_DELETED);
  }

  @Test
  @DisplayName("学生IDで学生情報が取得できること")
  void 学生IDで学生情報が取得できること() {
    Student student = repository.getStudentById(EXISTING_STUDENT_ID_1);

    assertNotNull(student, "学生が取得できるはず");
    assertEquals(STUDENT_NAME_TARO, student.getName());
    assertEquals(STUDENT_EMAIL_TARO, student.getEmail());
  }

  @Test
  @DisplayName("存在しない学生IDで検索するとnullが返ること")
  void 存在しない学生IDで検索するとnullが返ること() {
    Student student = repository.getStudentById(NON_EXISTING_STUDENT_ID);

    assertNull(student, "存在しないIDの場合nullが返るはず");
  }

  // ============ 登録系テスト ============

  @Test
  @DisplayName("学生情報が登録できること")
  void 学生情報が登録できること() {
    Student student = createNewStudent("新規太郎", "シンキタロウ",
        "shinki@test.com", "北海道", 18);

    int result = repository.saveStudent(student);

    assertEquals(1, result, "1件登録されるはず");
    assertNotNull(student.getId(), "IDが自動採番されるはず");

    Student saved = repository.getStudentById(student.getId());
    assertNotNull(saved, "登録した学生が取得できるはず");
    assertEquals("新規太郎", saved.getName());
    assertEquals("shinki@test.com", saved.getEmail());
  }

  @Test
  @DisplayName("コース情報が登録できること")
  void コース情報が登録できること() {
    StudentCourse course = createNewCourse(EXISTING_STUDENT_ID_1, COURSE_NAME_WEBAPP,
        LocalDate.of(2025, 7, 1), LocalDate.of(2025, 9, 30));

    int result = repository.saveCourse(course);

    assertEquals(1, result, "1件登録されるはず");

    List<StudentCourse> courses = repository.getCoursesByStudentId(EXISTING_STUDENT_ID_1);
    assertCourseListContains(courses, COURSE_NAME_WEBAPP);
  }

  // ============ 更新系テスト ============

  @Test
  @DisplayName("学生情報が更新できること")
  void 学生情報が更新できること() {
    Student student = repository.getStudentById(EXISTING_STUDENT_ID_1);
    assertNotNull(student, "更新対象の学生が存在するはず");

    student.setName("更新太郎");
    student.setAge(21);

    int result = repository.updateStudent(student);

    assertEquals(1, result, "1件更新されるはず");

    Student updated = repository.getStudentById(EXISTING_STUDENT_ID_1);
    assertEquals("更新太郎", updated.getName());
    assertEquals(21, updated.getAge());
    assertEquals(STUDENT_EMAIL_TARO, updated.getEmail(), "更新していない項目は変わらないはず");
  }

  @Test
  @DisplayName("存在しない学生を更新しようとすると0件になること")
  void 存在しない学生を更新しようとすると0件になること() {
    Student student = createNewStudent("存在しない太郎", "ソンザイシナイタロウ",
        "nai@test.com", "不明", 20);
    student.setId(NON_EXISTING_STUDENT_ID);

    int result = repository.updateStudent(student);

    assertEquals(0, result, "存在しないIDの場合0件になるはず");
  }

  @Test
  @DisplayName("コース情報が更新できること")
  void コース情報が更新できること() {
    List<StudentCourse> courses = repository.getCoursesByStudentId(EXISTING_STUDENT_ID_1);
    assertFalse(courses.isEmpty(), "学生1にコースが存在するはず");

    StudentCourse course = courses.get(0);
    course.setCourseName(COURSE_NAME_JAVA_ADVANCED);

    int result = repository.updateCourse(course);

    assertEquals(1, result, "1件更新されるはず");

    List<StudentCourse> updated = repository.getCoursesByStudentId(EXISTING_STUDENT_ID_1);
    assertCourseListContains(updated, COURSE_NAME_JAVA_ADVANCED);
  }

  // ============ 削除系テスト ============

  @Test
  @DisplayName("学生を論理削除できること")
  void 学生を論理削除できること() {
    int result = repository.deleteStudent(EXISTING_STUDENT_ID_1);

    assertEquals(1, result, "1件削除されるはず");

    List<Student> students = repository.getActiveStudents();
    assertFalse(students.stream().anyMatch(s -> s.getId() == EXISTING_STUDENT_ID_1),
        "削除した学生は有効一覧に含まれないはず");
  }

  @Test
  @DisplayName("存在しない学生を削除しようとすると0件になること")
  void 存在しない学生を削除しようとすると0件になること() {
    int result = repository.deleteStudent(NON_EXISTING_STUDENT_ID);

    assertEquals(0, result, "存在しないIDの場合0件になるはず");
  }

  @Test
  @DisplayName("既に削除済みの学生を再度削除しても問題ないこと")
  void 既に削除済みの学生を再度削除しても問題ないこと() {
    int result = repository.deleteStudent(EXISTING_STUDENT_ID_3);

    assertEquals(1, result, "1件更新されるはず");

    List<Student> students = repository.getActiveStudents();
    assertFalse(students.stream().anyMatch(s -> s.getId() == EXISTING_STUDENT_ID_3),
        "削除した学生は有効一覧に含まれないはず");
  }

  // ============ コース取得系テスト ============

  @Test
  @DisplayName("全コース情報が取得できること")
  void 全コース情報が取得できること() {
    List<StudentCourse> courses = repository.getAllCourses();

    assertEquals(2, courses.size(), "初期データで2件登録されているはず");
    assertCourseListContains(courses, COURSE_NAME_JAVA);
    assertCourseListContains(courses, COURSE_NAME_SPRING);
  }

  @Test
  @DisplayName("特定学生のコース情報が取得できること")
  void 特定学生のコース情報が取得できること() {
    List<StudentCourse> courses = repository.getCoursesByStudentId(EXISTING_STUDENT_ID_1);

    assertEquals(1, courses.size(), "学生1のコースは1件のはず");
    assertCourseListContains(courses, COURSE_NAME_JAVA);
    assertTrue(courses.stream().allMatch(c -> c.getStudentId() == EXISTING_STUDENT_ID_1),
        "全てのコースのstudent_idが1のはず");
  }

  @Test
  @DisplayName("コースを持たない学生のコース検索は空リストになること")
  void コースを持たない学生のコース検索は空リストになること() {
    List<StudentCourse> courses = repository.getCoursesByStudentId(EXISTING_STUDENT_ID_3);

    assertNotNull(courses, "nullではなく空リストが返るはず");
    assertEquals(0, courses.size(), "コースを持たない学生は0件のはず");
  }

  @Test
  @DisplayName("getAllCoursesでenrollmentStatusが正しくマッピングされること")
  void getAllCoursesでenrollmentStatusが正しくマッピングされること() {
    List<StudentCourse> courses = repository.getAllCourses();

    assertFalse(courses.isEmpty(), "コースが取得できること");

    // enrollmentStatusが存在するコースを取得
    StudentCourse courseWithStatus = courses.stream()
        .filter(c -> c.getEnrollmentStatus() != null)
        .findFirst()
        .orElseThrow(() -> new AssertionError("申込状況付きコースが見つかりません"));

    // 全フィールドが正しく設定されているか確認
    EnrollmentStatus status = courseWithStatus.getEnrollmentStatus();
    assertNotNull(status.getId(), "enrollmentStatus.idが設定されていること");
    assertNotNull(status.getCourseId(), "enrollmentStatus.courseIdが設定されていること");
    assertNotNull(status.getStatus(), "enrollmentStatus.statusが設定されていること");

    // courseIdとcourseのidが一致すること
    assertEquals(courseWithStatus.getId(), status.getCourseId(),
        "courseIdが正しく紐づいていること");

    logger.info("マッピング確認: course.id={}, enrollmentStatus={}",
        courseWithStatus.getId(), status);
  }
}