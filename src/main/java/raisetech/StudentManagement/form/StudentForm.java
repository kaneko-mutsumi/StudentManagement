package raisetech.StudentManagement.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;

@Getter
@Setter
public class StudentForm {

  @NotBlank(message = "名前は必須です")
  private String name;

  @NotBlank(message = "カナ名は必須です")
  private String kanaName;

  // ニックネームは任意
  private String nickname;

  @Email(message = "正しいメールアドレスを入力してください")
  @NotBlank(message = "メールアドレスは必須です")
  private String email;

  @NotBlank(message = "地域は必須です")
  private String area;

  @Min(value = 1, message = "年齢は1歳以上で入力してください")
  private int age;

  @NotBlank(message = "性別を選択してください")
  private String sex;

  private String remark;

  // entity変換
  public Student toEntity() {
    Student student = new Student();
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
}
