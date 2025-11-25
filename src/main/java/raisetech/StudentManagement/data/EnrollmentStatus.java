package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentStatus {
  private Integer id;
  private int courseId;
  private String status;
}