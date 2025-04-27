package raisetech.StudentManagement.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import raisetech.StudentManagement.data.StudentsCourses;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsCoursesForm {

  @NotBlank(message = "コース名を選択してください")
  private String courseName;

  @NotNull(message = "開始日を入力してください")
  private LocalDate startDate;

  @NotNull(message = "終了日を入力してください")
  private LocalDate endDate;

  // Entity変換メソッド
  public StudentsCourses toEntity(int studentId) {
    StudentsCourses course = new StudentsCourses();
    course.setStudentId(studentId);
    course.setCourseName(this.courseName);
    course.setStartDate(this.startDate);
    course.setEndDate(this.endDate);
    return course;
  }
}
