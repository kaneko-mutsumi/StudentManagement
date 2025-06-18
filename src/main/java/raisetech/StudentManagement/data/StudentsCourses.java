package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class StudentsCourses {

  private Integer id;
  private int studentId;
  private String courseName;
  private LocalDate courseStartAt;
  private LocalDate courseEndAt;

  public StudentsCourses() {
  }

  @Override
  public String toString() {
    return "StudentsCourses{" +
        "id=" + id +
        ", studentId=" + studentId +
        ", courseName='" + courseName + '\'' +
        ", courseStartAt=" + courseStartAt +
        ", courseEndAt=" + courseEndAt +
        '}';
  }
}