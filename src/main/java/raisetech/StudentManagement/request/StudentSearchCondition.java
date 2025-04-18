package raisetech.StudentManagement.model;

import lombok.Data;

@Data
public class StudentSearchCondition {
  private Integer age;         // 10, 20, 30などの年代
  private String courseName;   // Javaコースなど
}
