package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.form.StudentForm;

@SpringBootTest
@Transactional
class StudentServiceTest {

  @Autowired
  private StudentService service;

  // ==========================================
  // 全メソッドのテスト（正常系）
  // ==========================================

  @Test
  void 学生一覧を取得できる() {
    // getStudents() のテスト
    List<Student> students = service.getStudents();

    assertNotNull(students);
    // data.sql にデータがあれば空でない
    assertFalse(students.isEmpty());
  }

  @Test
  void コース一覧を取得できる() {
    // getCourses() のテスト ← これが抜けていた！
    List<StudentCourse> courses = service.getCourses();

    assertNotNull(courses);
    // data.sql にコースデータがあれば空でない
    assertFalse(courses.isEmpty());
  }

  @Test
  void 学生詳細を取得できる() {
    // getStudentForm(int id) のテスト
    List<Student> students = service.getStudents();
    assertTrue(students.size() > 0, "テストデータが必要です");

    int existingId = students.get(0).getId();
    StudentForm form = service.getStudentForm(existingId);

    assertNotNull(form);
    assertNotNull(form.getName());
    assertEquals(existingId, form.getId());
  }

  @Test
  void 学生を登録できる() {
    // registerStudent(StudentForm form) のテスト
    StudentForm form = createValidForm("日向翔陽");

    assertDoesNotThrow(() -> service.registerStudent(form));

    // 登録されたことを確認
    List<Student> students = service.getStudents();
    assertTrue(students.stream()
            .anyMatch(s -> "日向翔陽".equals(s.getName())),
        "登録した学生が見つかりません");
  }

  @Test
  void 学生を更新できる() {
    // updateStudent(StudentForm form) のテスト
    List<Student> students = service.getStudents();
    assertTrue(students.size() > 0, "テストデータが必要です");

    int id = students.get(0).getId();
    StudentForm form = service.getStudentForm(id);
    form.setName("更新後の名前");

    assertDoesNotThrow(() -> service.updateStudent(form));

    // 更新されたことを確認
    StudentForm updated = service.getStudentForm(id);
    assertEquals("更新後の名前", updated.getName());
  }

  @Test
  void 学生を削除できる() {
    // deleteStudent(int id) のテスト
    List<Student> students = service.getStudents();
    assertTrue(students.size() > 0, "テストデータが必要です");

    int id = students.get(0).getId();

    assertDoesNotThrow(() -> service.deleteStudent(id));

    // 削除されたことを確認（取得するとエラー）
    assertThrows(ResourceNotFoundException.class,
        () -> service.getStudentForm(id),
        "削除した学生が取得できてしまいます");
  }

  // ==========================================
  // エラーケースのテスト（異常系）
  // ==========================================

  @Test
  void 存在しない学生の詳細取得はエラー() {
    assertThrows(ResourceNotFoundException.class,
        () -> service.getStudentForm(99999),
        "存在しないIDでResourceNotFoundExceptionが発生すること");
  }

  @Test
  void 存在しない学生の更新はエラー() {
    StudentForm form = new StudentForm();
    form.setId(99999);
    form.setName("存在しない学生");
    form.setAge(20);

    assertThrows(ResourceNotFoundException.class,
        () -> service.updateStudent(form),
        "存在しないIDでResourceNotFoundExceptionが発生すること");
  }

  @Test
  void 存在しない学生の削除はエラー() {
    assertThrows(ResourceNotFoundException.class,
        () -> service.deleteStudent(99999),
        "存在しないIDでResourceNotFoundExceptionが発生すること");
  }

  @Test
  void null入力での登録はエラー() {
    assertThrows(IllegalArgumentException.class,
        () -> service.registerStudent(null),
        "nullでIllegalArgumentExceptionが発生すること");
  }

  @Test
  void IDなしでの更新はエラー() {
    StudentForm form = new StudentForm();
    form.setName("名前だけ");
    // form.setId() を呼ばない → null

    assertThrows(IllegalArgumentException.class,
        () -> service.updateStudent(form),
        "IDなしでIllegalArgumentExceptionが発生すること");
  }

  // ==========================================
  // 統合シナリオテスト
  // ==========================================

  @Test
  void 学生の登録から削除までの一連の流れ() {
    // 1. 登録
    StudentForm form = createValidForm("影山飛雄");
    service.registerStudent(form);

    // 2. 一覧取得で存在確認
    List<Student> students = service.getStudents();
    Student registered = students.stream()
        .filter(s -> "影山飛雄".equals(s.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("登録した学生が見つかりません"));

    // 3. 詳細取得
    StudentForm detail = service.getStudentForm(registered.getId());
    assertEquals("影山飛雄", detail.getName());

    // 4. 更新
    detail.setName("影山飛雄（更新）");
    service.updateStudent(detail);

    StudentForm updated = service.getStudentForm(registered.getId());
    assertEquals("影山飛雄（更新）", updated.getName());

    // 5. 削除
    service.deleteStudent(registered.getId());

    assertThrows(ResourceNotFoundException.class,
        () -> service.getStudentForm(registered.getId()));
  }

  // ==========================================
  // ヘルパーメソッド
  // ==========================================

  private StudentForm createValidForm(String name) {
    StudentForm form = new StudentForm();
    form.setName(name);
    form.setKanaName("テスト");
    form.setNickname("ニックネーム");
    form.setEmail(name.replace(" ", "") + "@example.com");
    form.setArea("東京都");
    form.setAge(20);
    form.setSex("男性");
    form.setRemark("テストデータ");
    form.setCourseName("Java入門");
    form.setCourseStartAt(LocalDate.now());
    form.setCourseEndAt(LocalDate.now().plusMonths(3));
    return form;
  }
}