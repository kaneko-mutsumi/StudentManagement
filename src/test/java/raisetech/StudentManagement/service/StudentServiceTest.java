package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @InjectMocks
  private StudentService sut;

  @Test
  void 学生登録が正常に動作すること() {
    // 事前準備：テスト用のデータを作る
    StudentForm form = new StudentForm();
    form.setName("テスト太郎");
    form.setKanaName("テストタロウ");
    form.setEmail("test@example.com");
    form.setArea("テスト市");
    form.setAge(20);
    form.setSex("男性");
    form.setCourseName("Java入門");

    // 事前準備：偽物のrepositoryの動作を設定
    doAnswer(invocation -> {
      Student student = invocation.getArgument(0);
      student.setId(1);
      return 1;
    }).when(repository).saveStudent(any(Student.class));

    when(repository.saveCourse(any(StudentCourse.class))).thenReturn(1);

    // 実行：registerStudentメソッドを呼ぶ
    sut.registerStudent(form);

    // 検証：repositoryのメソッドが呼ばれたか確認
    verify(repository, times(1)).saveStudent(any(Student.class));
    verify(repository, times(1)).saveCourse(any(StudentCourse.class));
  }

  @Test
  void 学生情報がnullの場合_エラーが発生すること() {
    // 準備：nullを用意
    StudentForm form = null;

    // 実行と検証：RuntimeExceptionが発生することを確認
    assertThrows(RuntimeException.class, () -> {
      sut.registerStudent(form);
    });
  }

  @Test
  void 学生情報の登録に失敗した場合_エラーが発生すること() {
    // 準備：正常なformを作成
    StudentForm form = new StudentForm();
    form.setName("テスト太郎");
    form.setKanaName("テストタロウ");
    form.setEmail("test@example.com");
    form.setArea("テスト市");
    form.setAge(20);
    form.setSex("男性");
    form.setCourseName("Java入門");

    // 準備：saveStudentが失敗する（0を返す）
    doAnswer(invocation -> {
      Student student = invocation.getArgument(0);
      student.setId(1);
      return 0;  // 失敗を表す0を返す
    }).when(repository).saveStudent(any(Student.class));

    // 実行と検証：RuntimeExceptionが発生することを確認
    assertThrows(RuntimeException.class, () -> {
      sut.registerStudent(form);
    });
  }

  @Test
  void コース情報の登録に失敗した場合_エラーが発生すること() {
    // 準備：正常なformを作成
    StudentForm form = new StudentForm();
    form.setName("テスト太郎");
    form.setKanaName("テストタロウ");
    form.setEmail("test@example.com");
    form.setArea("テスト市");
    form.setAge(20);
    form.setSex("男性");
    form.setCourseName("Java入門");

    // 準備：saveStudentは成功する
    doAnswer(invocation -> {
      Student student = invocation.getArgument(0);
      student.setId(1);
      return 1;  // 成功
    }).when(repository).saveStudent(any(Student.class));

    // 準備：saveCourseは失敗する（0を返す）
    when(repository.saveCourse(any(StudentCourse.class))).thenReturn(0);

    // 実行と検証：RuntimeExceptionが発生することを確認
    assertThrows(RuntimeException.class, () -> {
      sut.registerStudent(form);
    });
  }

  @Test
  void 学生削除が正常に動作すること() {
    // 準備：削除するIDを用意
    int id = 1;

    // 準備：deleteStudentが成功する（1を返す）
    when(repository.deleteStudent(id)).thenReturn(1);

    // 実行：deleteStudentメソッドを呼ぶ
    sut.deleteStudent(id);

    // 検証：repositoryのdeleteStudentが1回呼ばれたか確認
    verify(repository, times(1)).deleteStudent(id);
  }

  @Test
  void 削除対象の学生が見つからない場合_エラーが発生すること() {
    // 準備：存在しないIDを用意
    int id = 999;

    // 準備：deleteStudentが失敗する（0を返す）
    when(repository.deleteStudent(id)).thenReturn(0);

    // 実行と検証：ResourceNotFoundExceptionが発生することを確認
    assertThrows(ResourceNotFoundException.class, () -> {
      sut.deleteStudent(id);
    });
  }

  @Test
  void 学生更新が正常に動作すること() {
    // 準備：更新用のformを作成（IDとcourseIdが必要）
    StudentForm form = new StudentForm();
    form.setId(1);  // 既存のID
    form.setName("更新太郎");
    form.setKanaName("コウシンタロウ");
    form.setEmail("update@example.com");
    form.setArea("更新市");
    form.setAge(25);
    form.setSex("男性");
    form.setCourseId(10);  // 既存のコースID
    form.setCourseName("Spring実践");

    // 準備：updateStudentが成功する（1を返す）
    when(repository.updateStudent(any(Student.class))).thenReturn(1);

    // 準備：updateCourseが成功する（1を返す）
    when(repository.updateCourse(any(StudentCourse.class))).thenReturn(1);

    // 実行：updateStudentメソッドを呼ぶ
    sut.updateStudent(form);

    // 検証：repositoryのメソッドが呼ばれたか確認
    verify(repository, times(1)).updateStudent(any(Student.class));
    verify(repository, times(1)).updateCourse(any(StudentCourse.class));
  }

  @Test
  void 更新対象の学生が見つからない場合_エラーが発生すること() {
    // 準備：存在しないIDのformを作成
    StudentForm form = new StudentForm();
    form.setId(999);
    form.setName("存在しない太郎");
    form.setKanaName("ソンザイシナイタロウ");
    form.setEmail("notfound@example.com");
    form.setArea("存在しない市");
    form.setAge(20);
    form.setSex("男性");
    form.setCourseName("Java入門");

    // 準備：updateStudentが失敗する（0を返す）
    when(repository.updateStudent(any(Student.class))).thenReturn(0);

    // 実行と検証：ResourceNotFoundExceptionが発生することを確認
    assertThrows(ResourceNotFoundException.class, () -> {
      sut.updateStudent(form);
    });
  }

  @Test
  void 更新時にformがnullの場合_エラーが発生すること() {
    // 準備：nullを用意
    StudentForm form = null;

    // 実行と検証：RuntimeExceptionが発生することを確認
    assertThrows(RuntimeException.class, () -> {
      sut.updateStudent(form);
    });
  }
}