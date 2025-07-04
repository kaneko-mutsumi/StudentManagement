package raisetech.StudentManagement.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 * 学生情報フォーム用のクラス（シンプル版）
 *
 */

@Getter
@Setter
public class StudentForm {

  private Integer id;
  private Integer courseId;

  @NotBlank(message = "名前は必須です")
  private String name;

  @NotBlank(message = "カナ名は必須です")
  private String kanaName;

  private String nickname;

  @Email(message = "正しいメールアドレスを入力してください")
  @NotBlank(message = "メールアドレスは必須です")
  private String email;

  @NotBlank(message = "地域は必須です")
  private String area;

  @NotNull(message = "年齢は必須です")
  @Min(value = 16, message = "年齢は16歳以上で入力してください")
  private Integer age;

  @NotBlank(message = "性別を選択してください")
  private String sex;

  private String remark;

  @NotBlank(message = "コース名を選択してください")
  private String courseName;

  @NotNull(message = "開始日を入力してください")
  private LocalDate courseStartAt;

  @NotNull(message = "終了日を入力してください")
  private LocalDate courseEndAt;

  /**
   * スペースを全角に変換するメソッド
   * 初心者向け：シンプルな文字列変換
   */
  private String fixSpaces(String text) {
    if (text == null) {
      return null;
    }
    return text.replace(" ", "　"); // 半角スペース→全角スペース
  }

  /**
   * ひらがなをカタカナに変換するメソッド
   * 初心者向け：基本的なひらがな→カタカナ変換
   */
  private String convertToKatakana(String text) {
    if (text == null) {
      return null;
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);

      // ひらがなの範囲（あ〜ん）をカタカナに変換
      if (c >= 'あ' && c <= 'ん') {
        // ひらがなからカタカナへの変換
        // 「あ」(12354) → 「ア」(12450) なので +96
        result.append((char)(c + 96));
      } else {
        // ひらがな以外はそのまま
        result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * 名前のsetterをカスタマイズ
   * スペースを全角に統一
   */
  public void setName(String name) {
    this.name = fixSpaces(name);
  }

  /**
   * カナ名のsetterをカスタマイズ
   * ひらがな→カタカナ変換 + スペース統一
   */
  public void setKanaName(String kanaName) {
    String converted = convertToKatakana(kanaName);
    this.kanaName = fixSpaces(converted);
  }

  public Student toStudentEntity() {
    Student student = new Student();
    student.setId(this.id);
    student.setName(this.name);
    student.setKanaName(this.kanaName);
    student.setNickname(this.nickname);
    student.setEmail(this.email);
    student.setArea(this.area);
    student.setAge(this.age);
    student.setSex(this.sex);
    student.setRemark(this.remark);
    student.setDeleted(false);
    return student;
  }

  public static StudentForm fromStudentDetail(StudentDetail detail) {
    StudentForm form = new StudentForm();

    Student student = detail.getStudent();
    form.setId(student.getId());
    form.setName(student.getName());
    form.setKanaName(student.getKanaName());
    form.setNickname(student.getNickname());
    form.setEmail(student.getEmail());
    form.setArea(student.getArea());
    form.setAge(student.getAge());
    form.setSex(student.getSex());
    form.setRemark(student.getRemark());

    StudentsCourses course = detail.getStudentsCourse();
    if (course != null) {
      form.setCourseId(course.getId());
      form.setCourseName(course.getCourseName());
      form.setCourseStartAt(course.getCourseStartAt());
      form.setCourseEndAt(course.getCourseEndAt());
    }

    return form;
  }

  public StudentsCourses toCourseEntity() {
    StudentsCourses course = new StudentsCourses();
    course.setId(this.courseId != null ? this.courseId : 0);
    course.setStudentId(this.id != null ? this.id : 0);
    course.setCourseName(this.courseName);
    course.setCourseStartAt(this.courseStartAt);
    course.setCourseEndAt(this.courseEndAt);
    return course;
  }

  public StudentDetail toStudentDetail() {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(toStudentEntity());
    detail.setStudentsCourse(toCourseEntity());
    return detail;
  }
}