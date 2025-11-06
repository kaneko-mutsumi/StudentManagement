package raisetech.StudentManagement.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository repository;

  // ============ 検索系テスト ============

  @Test
  @DisplayName("有効な学生一覧が取得できること")
  void 有効な学生一覧が取得できること() {
    // 実行
    List<Student> students = repository.getActiveStudents();

    // 検証
    assertEquals(2, students.size());  // 削除済みを除く2件
    assertEquals("テスト太郎", students.get(0).getName());
  }

  @Test
  @DisplayName("学生IDで学生情報が取得できること")
  void 学生IDで学生情報が取得できること() {
    // 実行
    Student student = repository.getStudentById(1);

    // 検証
    assertNotNull(student);
    assertEquals("テスト太郎", student.getName());
    assertEquals("taro@test.com", student.getEmail());
  }

  @Test
  @DisplayName("存在しない学生IDで検索するとnullが返ること")
  void 存在しない学生IDで検索するとnullが返ること() {
    // 実行
    Student student = repository.getStudentById(999);

    // 検証
    assertNull(student);
  }

  // ============ 登録系テスト ============

  @Test
  @DisplayName("学生情報が登録できること")
  void 学生情報が登録できること() {
    // 準備: 登録する学生データを作る
    Student student = new Student();
    student.setName("新規太郎");
    student.setKanaName("シンキタロウ");
    student.setEmail("shinki@test.com");
    student.setArea("北海道");
    student.setAge(18);
    student.setSex("男性");

    // 実行: データベースに登録
    int result = repository.saveStudent(student);

    // 検証1: 1件登録されたか
    assertEquals(1, result);

    // 検証2: IDが自動で付与されているか
    assertNotNull(student.getId());

    // 検証3: 本当にDBに保存されたか確認
    Student saved = repository.getStudentById(student.getId());
    assertEquals("新規太郎", saved.getName());
    assertEquals("shinki@test.com", saved.getEmail());
  }

  @Test
  @DisplayName("コース情報が登録できること")
  void コース情報が登録できること() {
    // 準備: 登録するコースデータを作る
    StudentCourse course = new StudentCourse();
    course.setStudentId(1);  // テスト太郎のID
    course.setCourseName("Webアプリ開発");
    course.setCourseStartAt(LocalDate.of(2025, 7, 1));
    course.setCourseEndAt(LocalDate.of(2025, 9, 30));

    // 実行: データベースに登録
    int result = repository.saveCourse(course);

    // 検証1: 1件登録されたか
    assertEquals(1, result);

    // 検証2: 本当にDBに保存されたか確認
    List<StudentCourse> courses = repository.getCoursesByStudentId(1);
    assertEquals(2, courses.size());

    // 検証3: 登録した内容が正しいか
    StudentCourse newCourse = courses.get(1);
    assertEquals("Webアプリ開発", newCourse.getCourseName());
  }

  // ============ 更新系テスト ============

  @Test
  @DisplayName("学生情報が更新できること")
  void 学生情報が更新できること() {
    // 準備: 更新する内容を作る
    Student student = repository.getStudentById(1);  // まず既存データを取得
    student.setName("更新太郎");  // 名前を変更
    student.setAge(21);  // 年齢を変更

    // 実行: データベースで更新
    int result = repository.updateStudent(student);

    // 検証1: 1件更新されたか
    assertEquals(1, result);

    // 検証2: 本当に更新されたか確認
    Student updated = repository.getStudentById(1);
    assertEquals("更新太郎", updated.getName());
    assertEquals(21, updated.getAge());

    // 検証3: 更新してない項目は変わってないか
    assertEquals("taro@test.com", updated.getEmail());  // メールは変わってないはず
  }

  @Test
  @DisplayName("存在しない学生を更新しようとすると0件になること")
  void 存在しない学生を更新しようとすると0件になること() {
    // 準備: 存在しないIDの学生データ
    Student student = new Student();
    student.setId(999);  // 存在しないID
    student.setName("存在しない太郎");
    student.setEmail("nai@test.com");
    student.setArea("不明");
    student.setAge(20);
    student.setSex("男性");

    // 実行: 更新しようとする
    int result = repository.updateStudent(student);

    // 検証: 0件（更新されなかった）
    assertEquals(0, result);
  }

  @Test
  @DisplayName("コース情報が更新できること")
  void コース情報が更新できること() {
    // 準備: 既存のコース情報を取得
    List<StudentCourse> courses = repository.getCoursesByStudentId(1);
    StudentCourse course = courses.get(0);  // 1件目を取得

    // コース名を変更
    course.setCourseName("Java応用");

    // 実行: データベースで更新
    int result = repository.updateCourse(course);

    // 検証1: 1件更新されたか
    assertEquals(1, result);

    // 検証2: 本当に更新されたか確認
    List<StudentCourse> updated = repository.getCoursesByStudentId(1);
    assertEquals("Java応用", updated.get(0).getCourseName());
  }

  // ============ 削除系テスト ============

  @Test
  @DisplayName("学生を論理削除できること")
  void 学生を論理削除できること() {
    // 実行: ID=1の学生を削除
    int result = repository.deleteStudent(1);

    // 検証1: 1件削除されたか
    assertEquals(1, result);

    // 検証2: 有効な学生一覧から消えているか
    List<Student> students = repository.getActiveStudents();
    assertEquals(1, students.size());

    // 検証3: でもデータ自体は残っているか（論理削除）
    Student deleted = repository.getStudentById(1);
    assertNotNull(deleted);
    assertEquals(Boolean.TRUE, deleted.getDeleted());
  }

  @Test
  @DisplayName("存在しない学生を削除しようとすると0件になること")
  void 存在しない学生を削除しようとすると0件になること() {
    // 実行: 存在しないIDを削除しようとする
    int result = repository.deleteStudent(999);

    // 検証: 0件（削除されなかった）
    assertEquals(0, result);
  }

  @Test
  @DisplayName("既に削除済みの学生を再度削除しても問題ないこと")
  void 既に削除済みの学生を再度削除しても問題ないこと() {
    // 実行: すでにdeleted=1のID=3を再度削除
    int result = repository.deleteStudent(3);

    // 検証: 1件更新される（deleted=1がまたdeleted=1になる）
    assertEquals(1, result);

    // 念のため確認
    Student deleted = repository.getStudentById(3);
    assertEquals(Boolean.TRUE, deleted.getDeleted());
  }

  // ============ コース取得系テスト ============

  @Test
  @DisplayName("全コース情報が取得できること")
  void 全コース情報が取得できること() {
    // 実行
    List<StudentCourse> courses = repository.getAllCourses();

    // 検証
    assertEquals(2, courses.size());  // data.sqlで2件登録している
    assertEquals("Java入門", courses.get(0).getCourseName());
    assertEquals("Spring実践", courses.get(1).getCourseName());
  }

  @Test
  @DisplayName("特定学生のコース情報が取得できること")
  void 特定学生のコース情報が取得できること() {
    // 実行
    List<StudentCourse> courses = repository.getCoursesByStudentId(1);

    // 検証
    assertEquals(1, courses.size());
    assertEquals("Java入門", courses.get(0).getCourseName());
    assertEquals(1, courses.get(0).getStudentId());
  }

  @Test
  @DisplayName("コースを持たない学生のコース検索は空リストになること")
  void コースを持たない学生のコース検索は空リストになること() {
    // 実行: コースを持たないID=3で検索
    List<StudentCourse> courses = repository.getCoursesByStudentId(3);

    // 検証: 空リスト
    assertNotNull(courses);
    assertEquals(0, courses.size());
  }
}