package raisetech.StudentManagement.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * 学生情報フォームクラス
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

}